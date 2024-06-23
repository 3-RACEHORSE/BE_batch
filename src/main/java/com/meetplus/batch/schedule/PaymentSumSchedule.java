package com.meetplus.batch.schedule;

import com.meetplus.batch.common.DateRangeUtil;
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
public class PaymentSumSchedule {

    private final JobLauncher jobLauncher;
    private final Job sumPaymentAmountPaidJob;
    private final JobExplorer jobExplorer;

    public PaymentSumSchedule(JobLauncher jobLauncher,
        @Qualifier("sumPaymentAmountPaidJob") Job sumPaymentAmountPaidJob,
        JobExplorer jobExplorer) {
        this.jobLauncher = jobLauncher;
        this.sumPaymentAmountPaidJob = sumPaymentAmountPaidJob;
        this.jobExplorer = jobExplorer;
    }

    @Scheduled(cron = "0 0 5 * * ?")
    public void runJob() {
        JobParameters jobParameters = new JobParametersBuilder()
            .addString("paymentJobStartTime", DateRangeUtil.getStartTime(5).toString())
            .addString("paymentJobEndTime", DateRangeUtil.getEndTime(5).toString())
            .addString("paymentSumTime",
                String.valueOf(System.currentTimeMillis()))
            .toJobParameters();

        JobInstance jobInstance = jobExplorer.getLastJobInstance("sumPaymentAmountPaidJob");
        if (jobInstance != null) {
            JobExecution jobExecution = jobExplorer.getLastJobExecution(jobInstance);
            if (jobExecution != null &&
                (jobExecution.getStatus() == BatchStatus.STOPPED ||
                    jobExecution.getStatus() == BatchStatus.FAILED
                )) {
                log.info(">>>>>>> run stopped or failed sumPaymentAmountPaidJob");
                runSumPaymentAmountPaidJob(jobExecution.getJobParameters());
            }
        }

        log.info(">>>>>>> run updatePaymentStatusJob");
        runSumPaymentAmountPaidJob(jobParameters);
    }

    private void runSumPaymentAmountPaidJob(JobParameters jobParameters) {
        try {
            jobLauncher.run(sumPaymentAmountPaidJob, jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException |
                 JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            log.error(">>>>>>> error with sumPaymentAmountPaidJob: {}", e.getMessage());
        }
    }
}
