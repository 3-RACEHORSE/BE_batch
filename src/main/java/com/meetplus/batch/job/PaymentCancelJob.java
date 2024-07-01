package com.meetplus.batch.job;

import com.meetplus.batch.common.CustomJobParameter;
import com.meetplus.batch.common.PaymentStatus;
import com.meetplus.batch.domain.payment.Payment;
import com.meetplus.batch.infrastructure.payment.PaymentRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Slf4j
public class PaymentCancelJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final PaymentRepository paymentRepository;
    private final EntityManagerFactory entityManagerFactory;
    private final CustomJobParameter customJobParameter;

    @Autowired
    public PaymentCancelJob(JobRepository jobRepository,
        @Qualifier("paymentTransactionManager") PlatformTransactionManager transactionManager,
        PaymentRepository paymentRepository,
        @Qualifier("paymentEntityManagerFactory") EntityManagerFactory entityManagerFactory,
        CustomJobParameter customJobParameter) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.paymentRepository = paymentRepository;
        this.entityManagerFactory = entityManagerFactory;
        this.customJobParameter = customJobParameter;
    }

    @Bean
    @JobScope
    public PaymentCancelItemReader paymentCancelReader() {
        return new PaymentCancelItemReader(paymentRepository, customJobParameter);
    }

    @Bean
    public ItemProcessor<Payment, Payment> paymentCancelProcessor() {
        return payment -> {
            payment.setPaymentStatus(PaymentStatus.CANCEL);
            return payment;
        };
    }

    @Bean
    public JpaItemWriter<Payment> paymentCancelWriter() {
        return new JpaItemWriterBuilder<Payment>()
            .entityManagerFactory(entityManagerFactory)
            .build();
    }

    @Bean
    @JobScope
    @Qualifier("updatePaymentStatusStep")
    public Step updatePaymentStatusStep() {
        return new StepBuilder("updatePaymentStatusStep", jobRepository)
            .<Payment, Payment>chunk(10, transactionManager)
            .reader(paymentCancelReader())
            .processor(paymentCancelProcessor())
            .writer(paymentCancelWriter())
            .allowStartIfComplete(true)
            .build();
    }

    @Bean
    @Qualifier("updatePaymentStatusJob")
    public Job updatePaymentStatusJob(
        @Qualifier("updatePaymentStatusStep") Step updatePaymentStatusStep) {
        return new JobBuilder("updatePaymentStatusJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(updatePaymentStatusStep)
            .build();
    }
}
