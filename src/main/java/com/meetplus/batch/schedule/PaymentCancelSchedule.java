package com.meetplus.batch.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PaymentCancelSchedule {

    private final JobLauncher jobLauncher;
    private final Job updatePaymentStatusJob;

    public PaymentCancelSchedule(JobLauncher jobLauncher,
                                 @Qualifier("updatePaymentStatusJob") Job updatePaymentStatusJob) {
        this.jobLauncher = jobLauncher;
        this.updatePaymentStatusJob = updatePaymentStatusJob;
    }


    @Scheduled(cron = "0 52 3 * * ?")
    public void runJob() throws Exception {
        System.out.println("job start");
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("paymentCancelTime", String.valueOf(System.currentTimeMillis()))  // 고유한 파라미터 추가
                .toJobParameters();

        try {
            jobLauncher.run(updatePaymentStatusJob, jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException |
                 JobInstanceAlreadyCompleteException e) {
            System.out.println(e.getMessage());
        }
    }
}
