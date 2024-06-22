package com.meetplus.batch.job;

import com.meetplus.batch.kafka.KafkaProducerCluster;
import com.meetplus.batch.kafka.Topics;
import com.meetplus.batch.kafka.dto.EventPreviewDto;
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

import java.util.Map;

@Slf4j
@Configuration
public class BeforeEventStartJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final KafkaProducerCluster producer;

    @Autowired
    public BeforeEventStartJob(JobRepository jobRepository,
        @Qualifier("batchTransactionManager") PlatformTransactionManager transactionManager,
        KafkaProducerCluster producer) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.producer = producer;
    }

    @Bean
    @Qualifier("eventPreviewStep")
    public Step eventPreviewStep() {
        return new StepBuilder("beforeEventStartStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    // JobParameters에서 auctionUuid 추출
                    Map<String, Object> jobParameters = chunkContext.getStepContext().getJobParameters();
                    String auctionUuid = String.valueOf(jobParameters.get("auctionUuid"));

                    // kafka 메세지 전송
                    producer.sendMessage(Topics.Constant.PAYMENT_SERVICE,
                            EventPreviewDto.builder().auctionUuid(auctionUuid).build());

                    log.info("Executing before event start job with auctionUuid: {}", auctionUuid);
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
