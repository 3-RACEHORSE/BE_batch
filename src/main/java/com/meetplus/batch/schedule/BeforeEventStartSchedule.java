package com.meetplus.batch.schedule;

import com.meetplus.batch.domain.batch.BeforeEventStart;
import com.meetplus.batch.infrastructure.batch.BeforeEventStartRepository;
import com.meetplus.batch.kafka.Topics;
import com.meetplus.batch.kafka.dto.NewAuctionPostDto;
import com.meetplus.batch.state.ScheduleTimeEnum;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Component
public class BeforeEventStartSchedule {
    private final JobLauncher jobLauncher;
    private final Job beforeEventStartJob;
    private final ThreadPoolTaskScheduler taskScheduler;
    private final BeforeEventStartRepository beforeEventStartRepository;
    private final ConsumerFactory<String, Object> consumerFactory;

    public BeforeEventStartSchedule(JobLauncher jobLauncher,
                                    @Qualifier("eventPreviewJob") Job beforeEventStartJob,
                                    ThreadPoolTaskScheduler taskScheduler,
                                    BeforeEventStartRepository beforeEventStartRepository,
                                    ConsumerFactory<String, Object> consumerFactory) {
        this.jobLauncher = jobLauncher;
        this.beforeEventStartJob = beforeEventStartJob;
        this.taskScheduler = taskScheduler;
        this.beforeEventStartRepository = beforeEventStartRepository;
        this.consumerFactory = consumerFactory;
    }

    public void scheduleJob(NewAuctionPostDto newAuctionPostDto) {
        // 실행 시간 설정
        LocalDateTime executionTime = newAuctionPostDto.getEventStartTime()
                .plusHours(ScheduleTimeEnum.BEFORE_EVENT_START_24.getTime());
        log.info("Scheduling job to run at >>> {}", executionTime);

        // delay가 0이 되면 Job 실행
        //todo
        // 지금은 메시지 받고 5초 뒤 작동하도록 진행했으나 실제로는 밑 주석으로 진행해야 한다.
//        long delay = Duration.between(LocalDateTime.now(), executionTime).toMillis();
        long delay = Duration.between(LocalDateTime.now(), LocalDateTime.now().plusSeconds(5)).toMillis();

        // delay가 음수인 경우 즉시 실행
        if (delay < 0) {
            log.warn("Execution time is in the past. Job will be executed immediately.");
            delay = 0;
        }

        // 지정한 시간에 작업 실행하도록 스케줄링
        taskScheduler.schedule(() -> {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("beforeEventStartTime", String.valueOf(System.currentTimeMillis()))
                    .addString("auctionUuid", newAuctionPostDto.getAuctionUuid())
                    .toJobParameters();

            try {
                jobLauncher.run(beforeEventStartJob, jobParameters);

                // jobState true로 갱신
                updateJobState(newAuctionPostDto.getAuctionUuid());
            } catch (JobExecutionAlreadyRunningException | JobRestartException |
                     JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
                log.error("Job Execution failed >>> {}", e.getMessage());
            }
        }, new Date(System.currentTimeMillis() + delay));       // 작업이 시작될 시간 설정
    }

    private void updateJobState(String auctionUuid) {
        Optional<BeforeEventStart> optionalSchedule = beforeEventStartRepository.findByAuctionUuid(auctionUuid);
        optionalSchedule.ifPresent(schedule -> {
            schedule.setJobState(true);
            beforeEventStartRepository.save(schedule);
        });
    }

    // 서비스 재시작 시, kafka 소모하지 않은 메시지와 Job이 실행되지 않은 데이터들 스케줄링 등록
    @PostConstruct
    public void handleMissedMessages() {
        // 초기 더미 데이터 저장
        // 테이블에 데이터가 없는 경우 에러가 발생한다.
//        beforeEventStartRepository.save(BeforeEventStart.builder()
//                .auctionUuid("test")
//                .eventStartTime(LocalDateTime.MIN)
//                .build());

        try {
            log.info("PostConstruct Beginning scheduling process start");

            // Job 실행되지 않은 데이터 스케줄링 등록
//            List<BeforeEventStart> schedules = beforeEventStartRepository.findByJobStateFalse();
//            for (BeforeEventStart schedule : schedules) {
//                scheduleJob(NewAuctionPostDto.builder()
//                        .auctionUuid(schedule.getAuctionUuid())
//                        .eventStartTime(schedule.getEventStartTime())
//                        .build());
//            }

            // 소모하지 않은 메시지 조회
            Consumer<String, Object> kafkaConsumer = consumerFactory.createConsumer();
            kafkaConsumer.subscribe(Collections.singletonList(Topics.Constant.AUCTION_POST_SERVICE));

            // 파티션 할당
            kafkaConsumer.poll(Duration.ofMillis(0)); // 파티션 할당의 트리거

            // 각 파티션의 현재 오프셋과 마지막 오프셋을 추출
            for (TopicPartition partition : kafkaConsumer.assignment()) {
                long committedOffset = kafkaConsumer.committed(partition).offset();
                long endOffset = kafkaConsumer.endOffsets(Collections.singletonList(partition)).get(partition);

                // 아직 소비되지 않은 메시지가 있는지 확인
                if (committedOffset < endOffset) {
                    kafkaConsumer.seek(partition, committedOffset); // 소비되지 않은 첫 번째 메시지로 이동

                    // 메시지 폴링 및 처리
                    while (true) {
                        ConsumerRecords<String, Object> records = kafkaConsumer.poll(Duration.ofMillis(100));
                        if (records.isEmpty()) {
                            break;
                        }

                        for (ConsumerRecord<String, Object> record : records) {
                            // 여기서 메시지를 처리
                            log.info("Processing Kafka record: {}", record.value());

                            // 메시지를 파싱하고 작업을 스케줄링합니다.
                            LinkedHashMap<String, Object> message = (LinkedHashMap<String, Object>) record.value();
                            try {
                                DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
                                OffsetDateTime offsetDateTime = OffsetDateTime.
                                        parse(message.get("eventStartTime").toString(), formatter);
                                LocalDateTime eventStartTime = offsetDateTime.toLocalDateTime();

                                NewAuctionPostDto newAuctionPostDto = NewAuctionPostDto.builder()
                                        .auctionUuid(message.get("auctionUuid").toString())
                                        .eventStartTime(eventStartTime)
                                        .build();

                                scheduleJob(newAuctionPostDto);
                            } catch (Exception e) {
                                log.error("Error parsing Kafka message: {}", e.getMessage());
                            }
                        }
                    }
                }
            }
            kafkaConsumer.close();
        } catch (Exception e) {
            log.error("Error handling missed messages >>> {}", e.getMessage());
        }
    }
}
