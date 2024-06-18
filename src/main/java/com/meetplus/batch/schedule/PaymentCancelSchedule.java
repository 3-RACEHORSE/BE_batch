package com.meetplus.batch.schedule;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentCancelSchedule {

    private final JobLauncher jobLauncher;
    private final Job updatePaymentStatusJob;
    private final JobRegistry jobRegistry;

    public PaymentCancelSchedule(JobLauncher jobLauncher,
        @Qualifier("updatePaymentStatusJob") Job updatePaymentStatusJob,
        JobRegistry jobRegistry
    ) {
        this.jobLauncher = jobLauncher;
        this.updatePaymentStatusJob = updatePaymentStatusJob;
        this.jobRegistry = jobRegistry;
    }

    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(){
        JobRegistryBeanPostProcessor jobProcessor = new JobRegistryBeanPostProcessor();
        jobProcessor.setJobRegistry(jobRegistry);
        return jobProcessor;
    }


    @Scheduled(cron = "0/3 0 0 * * ?")
    public void runJob() throws Exception {
        try {
            Job job = jobRegistry.getJob("updatePaymentStatusJob"); // job 이름
            JobParametersBuilder jobParam = new JobParametersBuilder().addString("runId", UUID.randomUUID().toString());
            jobLauncher.run(job, jobParam.toJobParameters());
        } catch (JobExecutionAlreadyRunningException | JobRestartException |
                 JobInstanceAlreadyCompleteException e) {
            log.info(e.getMessage());
        }
    }
}
