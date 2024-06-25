package com.meetplus.batch.schedule;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
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
public class DonationAccountingSchedule {

    private final JobLauncher jobLauncher;
    private final Job donationAccountingJob;
    private final JobExplorer jobExplorer;

    public DonationAccountingSchedule(JobLauncher jobLauncher,
        @Qualifier("DonationAccountingJob") Job donationAccountingJob,
        JobExplorer jobExplorer) {
        this.jobLauncher = jobLauncher;
        this.donationAccountingJob = donationAccountingJob;
        this.jobExplorer = jobExplorer;
    }

    @Scheduled(cron = "0 0 8 * * ?", zone = "Asia/Seoul")
    public void runJob() {
        JobParameters jobParameters = new JobParametersBuilder()
            .addString("DonationAccountingUuid", UUID.randomUUID().toString()).toJobParameters();

        runDonationAccountingJob(jobParameters);
    }

    private void runDonationAccountingJob(JobParameters jobParameters) {
        try {
            jobLauncher.run(donationAccountingJob, jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException |
                 JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            log.error(">>>>>>> error with sumPaymentAmountPaidJob: {}", e.getMessage());
        }
    }
}
