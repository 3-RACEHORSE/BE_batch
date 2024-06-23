package com.meetplus.batch.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentCancelSchedule {

    private final JobLauncher jobLauncher;
    private final Job updatePaymentStatusJob;
    private final JobExplorer jobExplorer;

    public PaymentCancelSchedule(JobLauncher jobLauncher,
        @Qualifier("updatePaymentStatusJob") Job updatePaymentStatusJob,
        JobExplorer jobExplorer
    ) {
        this.jobLauncher = jobLauncher;
        this.updatePaymentStatusJob = updatePaymentStatusJob;
        this.jobExplorer = jobExplorer;
    }

    @Scheduled(cron = "0 0 5 * * ?")
    public void runJob() {
        JobParameters jobParameters = new JobParametersBuilder().addLong("paymentCancelTime",
            System.currentTimeMillis()).toJobParameters();

        //중단 또는 실패한 직전 job 재실행
        JobInstance jobInstance = jobExplorer.getLastJobInstance("updatePaymentStatusJob");
        if (jobInstance != null) {
            JobExecution jobExecution = jobExplorer.getLastJobExecution(jobInstance);
            if (jobExecution != null && 
                (jobExecution.getStatus() == BatchStatus.STOPPED ||
                    jobExecution.getStatus() == BatchStatus.FAILED
                )) {
                log.info(">>>>>>> run stopped or failed updatePaymentStatusJob");
                runUpdatePaymentStatusJob(jobExecution.getJobParameters());
            }
        }

        //예정된 job 실행
        log.info(">>>>>>> run updatePaymentStatusJob");
        runUpdatePaymentStatusJob(jobParameters);
    }

    private void runUpdatePaymentStatusJob(JobParameters jobParameters) {
        try {
            jobLauncher.run(updatePaymentStatusJob, jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException |
                 JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            log.error(">>>>>>> error with updatePaymentStatusJob: {}", e.getMessage());
        }
    }
}
