package com.meetplus.batch.schedule;

import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Component
public class PaymentSumSchedule {

    private final JobLauncher jobLauncher;
    private final Job sumPaymentAmountPaidJob;

    public PaymentSumSchedule(JobLauncher jobLauncher,
        @Qualifier("sumPaymentAmountPaidJob") Job sumPaymentAmountPaidJob) {
        this.jobLauncher = jobLauncher;
        this.sumPaymentAmountPaidJob = sumPaymentAmountPaidJob;
    }

    @Scheduled(cron = "0 29 18 * * ?")
    public void runJob() throws Exception {
        try {
            log.info(">>>>>>>> Running job");
            JobParameters jobParameters = new JobParametersBuilder()
                .addString("paymentSumTime",
                    String.valueOf(System.currentTimeMillis()))  // 고유한 파라미터 추가
                .toJobParameters();

            jobLauncher.run(sumPaymentAmountPaidJob, jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException |
                 JobInstanceAlreadyCompleteException e) {
            System.out.println(e.getMessage());
        }
    }
}
