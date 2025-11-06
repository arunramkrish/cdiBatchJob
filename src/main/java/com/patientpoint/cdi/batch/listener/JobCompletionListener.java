package com.patientpoint.cdi.batch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class JobCompletionListener implements JobExecutionListener {
    
    private static final Logger logger = LoggerFactory.getLogger(JobCompletionListener.class);
    
    @Override
    public void beforeJob(JobExecution jobExecution) {
        logger.info("Starting Elasticsearch sync job: {}", jobExecution.getJobInstance().getJobName());
    }
    
    @Override
    public void afterJob(JobExecution jobExecution) {
        logger.info("Job completed with status: {}", jobExecution.getStatus());
        Instant endTime = jobExecution.getEndTime();
        Instant startTime = jobExecution.getStartTime();
        if (endTime != null && startTime != null) {
            Duration duration = Duration.between(startTime, endTime);
            logger.info("Job execution time: {} ms", duration.toMillis());
        }
    }
}

