package com.meetplus.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentCancelSchedule {

    private final JobLauncher jobLauncher;

    private final Job updatePaymentStatusJob;

    @Scheduled(cron = "0 0 4 * * ?")
    public void runJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
            .addString("paymentCancelTime", String.valueOf(System.currentTimeMillis()))  // 고유한 파라미터 추가
            .toJobParameters();

        try {
            log.info("joLauncher start");
            jobLauncher.run(updatePaymentStatusJob, jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException |
                 JobInstanceAlreadyCompleteException e) {
            System.out.println(e.getMessage());
        }
    }
}
