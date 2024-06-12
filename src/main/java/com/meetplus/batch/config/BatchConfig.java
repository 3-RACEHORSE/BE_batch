package com.meetplus.batch.config;

import com.meetplus.batch.common.DateRangeUtil;
import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@ComponentScan(basePackages = {"com.meetplus.batch.config", "com.meetplus.batch.infrastructure",
    "com.meetplus.batch"})
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;
    private final EntityManagerFactory entityManagerFactory;

    public BatchConfig(JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        @Qualifier("paymentDataSource") DataSource dataSource,
        EntityManagerFactory entityManagerFactory) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.dataSource = dataSource;
        this.entityManagerFactory = entityManagerFactory;
    }

    @Bean
    public JobParameters jobParameters() {
        return new JobParametersBuilder()
            .addLocalDateTime("startTime", DateRangeUtil.getStartTime())
            .addLocalDateTime("endTime", DateRangeUtil.getEndTime())
            .toJobParameters();
    }
}
