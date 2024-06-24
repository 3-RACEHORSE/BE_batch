package com.meetplus.batch.job;

import com.meetplus.batch.common.PaymentStatus;
import com.meetplus.batch.domain.payment.Payment;
import com.meetplus.batch.infrastructure.payment.PaymentRepository;
import com.meetplus.batch.kafka.KafkaProducerCluster;
import com.meetplus.batch.kafka.Topics;
import com.meetplus.batch.kafka.Topics.Constant;
import com.meetplus.batch.kafka.dto.AlarmDto;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class BeforeEventStartJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final KafkaProducerCluster producer;
    private final PaymentRepository paymentRepository;

    @Autowired
    public BeforeEventStartJob(JobRepository jobRepository,
        @Qualifier("batchTransactionManager") PlatformTransactionManager transactionManager,
        KafkaProducerCluster producer,
        PaymentRepository paymentRepository) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.producer = producer;
        this.paymentRepository = paymentRepository;
    }

    @Bean
    @Qualifier("eventPreviewStep")
    public Step eventPreviewStep() {
        return new StepBuilder("beforeEventStartStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                Map<String, Object> jobParameters = chunkContext.getStepContext()
                    .getJobParameters();
                String auctionUuid = String.valueOf(jobParameters.get("auctionUuid"));

                List<Payment> payments = paymentRepository.findByAuctionUuidAndPaymentStatus(
                    auctionUuid, PaymentStatus.COMPLETE);
                if (payments.isEmpty()) {
                    log.info("there is no payment for auction uuid: {}", auctionUuid);
                    return RepeatStatus.FINISHED;
                }

                AlarmDto alarmDto = AlarmDto.builder()
                    .eventType("Event Preview Alarm")
                    .message("행사 시작까지 24시간 남았어요.")
                    .receiverUuids(payments.stream().map(Payment::getMemberUuid).toList())
                    .build();

                // kafka 메세지 전송
                producer.sendMessage(Constant.ALARM_TOPIC, alarmDto);
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }

    @Bean
    @Qualifier("eventPreviewJob")
    public Job eventPreviewJob(@Qualifier("eventPreviewStep") Step beforeEventStartStep) {
        return new JobBuilder("beforeEventStartJob", jobRepository)
            .start(beforeEventStartStep)
            .build();
    }
}
