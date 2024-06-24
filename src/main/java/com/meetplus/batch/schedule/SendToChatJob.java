package com.meetplus.batch.schedule;

import com.meetplus.batch.common.CustomJobParameter;
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
        List<String> auctionUuids = paymentRepository.getAuctionUuidsByDateRange(
            customJobParameter.getStartTime().minusHours(3),
            customJobParameter.getEndTime().minusHours(3));
        log.info("auctionUuids: {}", auctionUuids);
        log.info("시작시간: {}", customJobParameter.getStartTime().minusHours(3));
        log.info("마감시간: {}", customJobParameter.getEndTime().minusHours(3));

        return new ItemReader<String>() {
            private List<String> auctionUuids = paymentRepository.getAuctionUuidsByDateRange(
                customJobParameter.getStartTime().minusDays(1).minusHours(3),
                customJobParameter.getEndTime().minusDays(1).minusHours(3));
            private int nextIndex = 0;

            @Override
            public String read() {
                if (nextIndex < auctionUuids.size()) {
                    return auctionUuids.get(nextIndex++);
                } else {
                    return null; // 모든 auctionUuid를 처리한 경우 null 반환
                }
            }
        };
    }

    @Bean
    @StepScope
    public ItemProcessor<String, Void> sendToChatProcessor(
    ) {
        return auctionUuid -> {
            try {
                log.info("Processing auctionUuid: {}", auctionUuid);
                List<String> memberUuids = paymentRepository.getMemberUuidsByAuctionUuidAndPaymentStatus(auctionUuid,PaymentStatus.COMPLETE);
                log.info("MemberUuids: {}", memberUuids.toString());

                    producer.sendMessage(Constant.SEND_TO_AUCTION_FOR_CREATE_CHATROOM,
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
            // 할게 없음
        };
    }

    @Bean
    @JobScope
    @Qualifier("sendToChatStep")
    public Step sendToChatStep() {
        return new StepBuilder("sendToChatStep", jobRepository)
            .<String, Void>chunk(10, transactionManager) // 한 번에 처리할 아이템 수를 지정합니다.
            .reader(sendToChatReader())
            .processor(sendToChatProcessor())
            .writer(sendToChatWriter())
            .build();
    }

    @Bean
    @Qualifier("sendToChatDataJob")
    public Job sendToChatDataJob(@Qualifier("sendToChatStep") Step sendToChatStep) {
        return new JobBuilder("sendToChatDataJob", jobRepository)
            .start(sendToChatStep) // 시작할 Step을 지정합니다.
            .build();
    }

}
