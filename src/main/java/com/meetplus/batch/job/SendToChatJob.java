package com.meetplus.batch.job;

import com.meetplus.batch.common.CustomJobParameter;
import com.meetplus.batch.common.DateRangeUtil;
import com.meetplus.batch.common.PaymentStatus;
import com.meetplus.batch.domain.payment.Bank;
import com.meetplus.batch.domain.payment.Payment;
import com.meetplus.batch.infrastructure.payment.PaymentRepository;
import com.meetplus.batch.kafka.KafkaProducerCluster;
import com.meetplus.batch.kafka.Topics;
import com.meetplus.batch.kafka.Topics.Constant;
import com.meetplus.batch.kafka.dto.SaveDonationDto;
import com.meetplus.batch.kafka.dto.SendToChatDto;
import jakarta.persistence.EntityManagerFactory;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Slf4j
public class SendToChatJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final PaymentRepository paymentRepository;
    private final EntityManagerFactory entityManagerFactory;
    private final CustomJobParameter customJobParameter;
    private final KafkaProducerCluster producer;

    private List<String> auctionUuids;

    @Autowired
    public SendToChatJob(JobRepository jobRepository,
        @Qualifier("paymentTransactionManager") PlatformTransactionManager transactionManager,
        PaymentRepository paymentRepository,
        @Qualifier("paymentEntityManagerFactory") EntityManagerFactory entityManagerFactory,
        CustomJobParameter customJobParameter, KafkaProducerCluster producer) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.paymentRepository = paymentRepository;
        this.entityManagerFactory = entityManagerFactory;
        this.customJobParameter = customJobParameter;
        this.producer = producer;
    }

    @Bean
    @StepScope
    public ItemReader<String> sendToChatReader() {
        if (auctionUuids == null) {
            auctionUuids = paymentRepository.getAuctionUuidsByDateRange(
                DateRangeUtil.getStartTime(2).minusDays(1),
                DateRangeUtil.getStartTime(2));
        }

        return new ItemReader<String>() {
            private int nextIndex = 0;

            @Override
            public String read() {
                if (nextIndex < auctionUuids.size()) {
                    return auctionUuids.get(nextIndex++);
                } else {
                    return null;
                }
            }
        };
    }

    @Bean
    @StepScope
    public ItemProcessor<String, Void> sendToChatProcessor() {
        return auctionUuid -> {
            try {
                List<String> memberUuids = paymentRepository.getMemberUuidsByAuctionUuidAndPaymentStatus(
                    auctionUuid, PaymentStatus.COMPLETE);

                producer.sendMessage(Constant.SEND_TO_AUCTION_POST_FOR_CREATE_CHATROOM,
                    SendToChatDto.builder()
                        .auctionUuid(auctionUuid)
                        .memberUuids(memberUuids)
                        .build());
                return null;
            } catch (Exception e) {
                log.info("Error processing auctionUuid: {}", e.getMessage());
                return null;
            }
        };
    }

    @Bean
    public ItemWriter<Void> sendToChatWriter() {
        return items -> {
        };
    }

    @Bean
    @JobScope
    @Qualifier("sendToChatStep")
    public Step sendToChatStep() {
        return new StepBuilder("sendToChatStep", jobRepository)
            .<String, Void>chunk(10, transactionManager)
            .reader(sendToChatReader())
            .processor(sendToChatProcessor())
            .writer(sendToChatWriter())
            .build();
    }

    @Bean
    @Qualifier("sendToChatDataJob")
    public Job sendToChatDataJob(@Qualifier("sendToChatStep") Step sendToChatStep) {
        return new JobBuilder("sendToChatDataJob", jobRepository)
            .start(sendToChatStep)
            .build();
    }
}