package com.meetplus.batch.schedule;

import com.meetplus.batch.kafka.dto.EventStartTimeDto;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BeforeEventStartSchedule {

    private final JobLauncher jobLauncher;
    private final Job beforeEventStartJob;
    private final ThreadPoolTaskScheduler taskScheduler;

    public BeforeEventStartSchedule(JobLauncher jobLauncher,
        @Qualifier("eventPreviewJob") Job beforeEventStartJob,
        ThreadPoolTaskScheduler taskScheduler) {
        this.jobLauncher = jobLauncher;
        this.beforeEventStartJob = beforeEventStartJob;
        this.taskScheduler = taskScheduler;
    }

    public void scheduleJob(EventStartTimeDto eventStartTimeDto) {

        // delay가 0이 되면 Job 실행
        long delay = eventStartTimeDto.getEventStartTime() - System.currentTimeMillis();

        // delay가 음수인 경우 즉시 실행
        if (delay < 0) {
            log.warn("Execution time is in the past. Job will be executed immediately.");
            delay = 0;
        }

        // 지정한 시간에 작업 실행하도록 스케줄링
        taskScheduler.schedule(() -> {
            JobParameters jobParameters = new JobParametersBuilder()
                .addString("beforeEventStartTime", String.valueOf(System.currentTimeMillis()))
                .addString("auctionUuid", eventStartTimeDto.getAuctionUuid())
                .toJobParameters();

            try {
                jobLauncher.run(beforeEventStartJob, jobParameters);
            } catch (JobExecutionAlreadyRunningException | JobRestartException |
                     JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
                log.error("Job Execution failed >>> {}", e.getMessage());
            }
        }, new Date(System.currentTimeMillis() + delay));
    }
}
