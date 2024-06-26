package com.meetplus.batch.schedule;

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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DonationTotalSchedule {

    private final JobLauncher jobLauncher;
    private final Job donationTotalJob;
    private final JobExplorer jobExplorer;

    public DonationTotalSchedule(JobLauncher jobLauncher,
        @Qualifier("donationTotalJob") Job donationTotalJob,
        JobExplorer jobExplorer) {
        this.jobLauncher = jobLauncher;
        this.donationTotalJob = donationTotalJob;
        this.jobExplorer = jobExplorer;
    }

    @Scheduled(cron = "0 17 14 * * ?", zone = "Asia/Seoul")
    public void runJob() {
        JobParameters jobParameters = new JobParametersBuilder()
            .addString("donationTotalUuid", UUID.randomUUID().toString()).toJobParameters();

        JobInstance jobInstance = jobExplorer.getLastJobInstance("totalDonationJob");
        if (jobInstance != null) {
            JobExecution jobExecution = jobExplorer.getLastJobExecution(jobInstance);
            if (jobExecution != null &&
                (jobExecution.getStatus() == BatchStatus.STOPPED ||
                    jobExecution.getStatus() == BatchStatus.FAILED
                )) {
                log.info(">>>>>>> run stopped or failed totalDonationJob");
                runDonationTotalJob(jobExecution.getJobParameters());
            }
        }

        log.info(">>>>>>> run sumPaymentStatusJob");
        runDonationTotalJob(jobParameters);
    }

    private void runDonationTotalJob(JobParameters jobParameters) {
        try {
            jobLauncher.run(donationTotalJob, jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException |
                 JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            log.error(">>>>>>> error with DonationAccountingJob: {}", e.getMessage());
        }
    }
}
