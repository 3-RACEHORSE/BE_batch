package com.meetplus.batch.job;

import com.meetplus.batch.common.CustomJobParameter;
import com.meetplus.batch.domain.payment.Bank;
import com.meetplus.batch.infrastructure.payment.BankRepository;
import com.meetplus.batch.infrastructure.payment.PaymentRepository;
import jakarta.persistence.EntityManagerFactory;
import java.math.BigDecimal;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Slf4j
public class PaymentSumJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final PaymentRepository paymentRepository;
    private final BankRepository bankRepository;
    private final EntityManagerFactory entityManagerFactory;
    private final CustomJobParameter customJobParameter;


    @Autowired
    public PaymentSumJob(JobRepository jobRepository,
        @Qualifier("paymentTransactionManager") PlatformTransactionManager transactionManager,
        PaymentRepository paymentRepository,
        BankRepository bankRepository,
        @Qualifier("paymentEntityManagerFactory") EntityManagerFactory entityManagerFactory,
        CustomJobParameter customJobParameter) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.paymentRepository = paymentRepository;
        this.bankRepository = bankRepository;
        this.entityManagerFactory = entityManagerFactory;
        this.customJobParameter = customJobParameter;
    }


    @Bean
    @StepScope
    public ItemReader<String> paymentSumReader() {
        return new ItemReader<String>() {
            private List<String> auctionUuids = paymentRepository.getAuctionUuidsByDateRange(
                customJobParameter.getPaymentJobStartTime(),
                customJobParameter.getPaymentJobEndTime());
            private int nextIndex = 0;

            @Override
            public String read() throws Exception {
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
    public ItemProcessor<String, Bank> paymentSumProcessor(
    ) {
        return auctionUuid -> {
            try {
                log.info("Processing auctionUuid: {}", auctionUuid);
                BigDecimal totalAmount = paymentRepository.getTotalAmountByAuctionUuid(
                    auctionUuid,
                    customJobParameter.getPaymentJobStartTime(),
                    customJobParameter.getPaymentJobEndTime()
                );
                Bank bank = Bank.builder()
                    .auctionUuid(auctionUuid)
                    .donation(totalAmount)
                    .build();
                bankRepository.save(bank);
                return bank;
            } catch (Exception e) {
                log.info("Error processing auctionUuid: {}", e.getMessage());
                return null;
            }
        };
    }

    @Bean
    public JpaItemWriter<Bank> paymentSumWriter() {
        return new JpaItemWriterBuilder<Bank>()
            .entityManagerFactory(entityManagerFactory)
            .build();
    }

    @Bean
    @JobScope
    @Qualifier("sumPaymentAmountPaidStep")
    public Step sumPaymentAmountPaidStep() {
        return new StepBuilder("sumPaymentAmountPaidStep", jobRepository)
            .<String, Bank>chunk(10, transactionManager)
            .reader(paymentSumReader())
            .processor(paymentSumProcessor())
            .writer(paymentSumWriter())
            .allowStartIfComplete(true)
            .build();
    }

    @Bean
    @Qualifier("sumPaymentAmountPaidJob")
    public Job sumPaymentAmountPaidJob(
        @Qualifier("sumPaymentAmountPaidStep") Step sumPaymentAmountPaidStep) {
        return new JobBuilder("sumPaymentAmountPaidJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(sumPaymentAmountPaidStep)
            .build();
    }
}
