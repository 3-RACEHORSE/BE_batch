package com.meetplus.batch.schedule;

import com.meetplus.batch.domain.Bank;
import com.meetplus.batch.infrastructure.BankRepository;
import com.meetplus.batch.infrastructure.PaymentRepository;
import jakarta.persistence.EntityManagerFactory;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
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
    private final JobParameters jobParameters;

    @Autowired
    public PaymentSumJob(JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        PaymentRepository paymentRepository,
        BankRepository bankRepository,
        EntityManagerFactory entityManagerFactory,
        JobParameters jobParameters) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.paymentRepository = paymentRepository;
        this.bankRepository = bankRepository;
        this.entityManagerFactory = entityManagerFactory;
        this.jobParameters = jobParameters;
    }


    @Bean
    public ItemReader<String> paymentSumReader() {
        return new ItemReader<String>() {
            private List<String> auctionUuids = paymentRepository.findAuctionUuidsByDateRange(
                jobParameters.getLocalDateTime("startTime"),
                jobParameters.getLocalDateTime("endTime"));
            private int nextIndex = 0;

            @Override
            public String read() throws Exception {
                if (nextIndex < auctionUuids.size()) {
                    return auctionUuids.get(nextIndex++);
                } else {
                    return null; // 모든 auctionUuid를 처리한 경우 null 반환
                }
            }
        };
    }

    @Bean
    public ItemProcessor<String, Bank> paymentSumProcessor() {
        return auctionUuid -> {
            try {
                BigDecimal totalAmount = paymentRepository.getTotalAmountByAuctionUuid(
                    auctionUuid,
                    jobParameters.getLocalDateTime("startTime"),
                    jobParameters.getLocalDateTime("endTime")
                );
                Optional<Bank> bankOpt = bankRepository.findByAuctionUuid(auctionUuid);
                if (bankOpt.isPresent()) {
                    Bank bank = bankOpt.get();
                    return Bank.builder()
                        .id(bank.getId())
                        .auctionUuid(bank.getAuctionUuid())
                        .donation(bank.getDonation().add(totalAmount))
                        .build();
                } else if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
                    Bank bank = Bank.builder()
                        .auctionUuid(auctionUuid)
                        .donation(totalAmount)
                        .build();
                    bankRepository.save(bank);
                    return bank;
                } else {
                    return null;
                }
            } catch (Exception e) {
                System.err.println("Error processing auctionUuid: " + auctionUuid);
                e.printStackTrace();
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
    @Qualifier("sumPaymentAmountPaidStep")
    public Step sumPaymentAmountPaidStep() {
        return new StepBuilder("sumPaymentAmountPaidStep", jobRepository)
            .<String, Bank>chunk(10, transactionManager)
            .reader(paymentSumReader())
            .processor(paymentSumProcessor())
            .writer(paymentSumWriter())
            .build();
    }

    @Bean
    @Qualifier("sumPaymentAmountPaidJob")
    public Job sumPaymentAmountPaidJob(@Qualifier("sumPaymentAmountPaidStep") Step sumPaymentAmountPaidStep) {
        return new JobBuilder("sumPaymentAmountPaidJob", jobRepository)
            .start(sumPaymentAmountPaidStep)
            .build();
    }
}
