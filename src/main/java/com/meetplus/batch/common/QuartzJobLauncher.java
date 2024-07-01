package com.meetplus.batch.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.quartz.QuartzJobBean;

@Slf4j
@Getter
@Setter
@RequiredArgsConstructor
public class QuartzJobLauncher extends QuartzJobBean {

    private String jobName;
    private final JobLauncher jobLauncher;
    private final JobLocator jobLocator;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        Date paymentDeadLine = new Date();
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Job job = jobLocator.getJob(jobName);
            JobParameters jobParameters = new JobParametersBuilder()
                .addDate("requestDate", paymentDeadLine)
                .toJobParameters();
            jobLauncher.run(job, jobParameters);
        } catch (Exception e) {
            log.info("error : {}", e);
        }
    }
}
