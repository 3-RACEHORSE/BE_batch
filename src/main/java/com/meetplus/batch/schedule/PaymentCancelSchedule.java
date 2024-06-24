package com.meetplus.batch.schedule;

import com.meetplus.batch.common.DateRangeUtil;
import java.util.UUID;
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
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
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

    @Scheduled(cron = "0 0 2 * * ?", zone = "Asia/Seoul")
    public void runJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
            .addString("paymentJobStartTime", DateRangeUtil.getStartTime(2).toString())
            .addString("paymentJobEndTime", DateRangeUtil.getEndTime(2).toString())
            .addString("paymentCancelUuid", UUID.randomUUID().toString()).toJobParameters();

        JobInstance jobInstance = jobExplorer.getLastJobInstance("updatePaymentStatusJob");
        if (jobInstance != null) {
            JobExecution jobExecution = jobExplorer.getLastJobExecution(jobInstance);
            if (jobExecution != null &&
                (jobExecution.getStatus() == BatchStatus.STOPPED ||
                    jobExecution.getStatus() == BatchStatus.FAILED
                )) {
                log.info(">>>>>>> run stopped or failed updatePaymentStatusJob");
                runUpdatePaymentAmountPaidJob(jobParameters);
            }
        }

        log.info(">>>>>>> run updatePaymentStatusJob");
        runUpdatePaymentAmountPaidJob(jobParameters);
    }

    private void runUpdatePaymentAmountPaidJob(JobParameters jobParameters) {
        try {
            jobLauncher.run(updatePaymentStatusJob, jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException |
                 JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            log.error(">>>>>>> error with updatePaymentStatusJob: {}", e.getMessage());
        }
    }
}
