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
public class SendToChatSchedule {
    private final JobLauncher jobLauncher;
    private final Job sendToChatDataJob;

    public SendToChatSchedule(JobLauncher jobLauncher,
        @Qualifier("sendToChatDataJob") Job sendToChatDataJob) {
        this.jobLauncher = jobLauncher;
        this.sendToChatDataJob = sendToChatDataJob;
    }

    @Scheduled(cron = "0 0 9 * * ?", zone = "Asia/Seoul")
    public void runJob() throws Exception {
        try {
            log.info(">>>>>>>> Running job");
            JobParameters jobParameters = new JobParametersBuilder()
                .addString("sendToChatTime",
                    String.valueOf(System.currentTimeMillis()))  // 고유한 파라미터 추가
                .toJobParameters();

            jobLauncher.run(sendToChatDataJob, jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException |
                 JobInstanceAlreadyCompleteException e) {
            System.out.println(e.getMessage());
        }
    }
}
