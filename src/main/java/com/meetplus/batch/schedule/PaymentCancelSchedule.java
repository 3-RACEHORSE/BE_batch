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

    @Scheduled(cron = "0 35 00 * * ?")
    public void runJob() throws Exception {
        log.info("jobNames: {}",jobRegistry.getJobNames());
        try {
            log.info("PaymentCancelSchedule의 runJob 실행");
            Job job = jobRegistry.getJob("updatePaymentStatusJob"); // job 이름

            if (job == null) {
                log.error("updatePaymentStatusJob이 JobRegistry에 등록되어 있지 않습니다.");

            }

            JobParameters jobParameters = new JobParametersBuilder().addString("runId", UUID.randomUUID().toString()).toJobParameters();
            jobLauncher.run(job, jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException |
                 JobInstanceAlreadyCompleteException e) {
            log.error("PaymentCancelSchedule의 runJob 실행 중 오류 발생: {}", e.getMessage());
        }
    }
}
