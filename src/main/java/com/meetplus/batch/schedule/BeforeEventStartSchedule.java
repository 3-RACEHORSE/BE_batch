package com.meetplus.batch.schedule;

import com.meetplus.batch.kafka.dto.NewAuctionPostDto;
import com.meetplus.batch.state.ScheduleTimeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@Component
public class BeforeEventStartSchedule {
    private final JobLauncher jobLauncher;
    private final Job beforeEventStartJob;
    private final ThreadPoolTaskScheduler taskScheduler;

    public BeforeEventStartSchedule(JobLauncher jobLauncher,
                                    @Qualifier("eventPreviewJob") Job beforeEventStartJob,
                                    ThreadPoolTaskScheduler taskScheduler) {
        this.jobLauncher = jobLauncher;
        this.beforeEventStartJob = beforeEventStartJob;
        this.taskScheduler = taskScheduler;
    }

    public void scheduleJob(NewAuctionPostDto newAuctionPostDto) {
        // 실행 시간 설정
        LocalDateTime executionTime = newAuctionPostDto.getEventStartTime()
                .plusHours(ScheduleTimeEnum.BEFORE_EVENT_START.getTime());
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
            } catch (JobExecutionAlreadyRunningException | JobRestartException |
                    JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
                log.error("Job Execution failed >>> {}", e.getMessage());
            }
        }, new Date(System.currentTimeMillis() + delay));       // 작업이 시작될 시간 설정
    }
}
