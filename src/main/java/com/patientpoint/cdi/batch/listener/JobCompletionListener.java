package com.patientpoint.cdi.batch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

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
        if (jobExecution.getEndTime() != null && jobExecution.getStartTime() != null) {
            logger.info("Job execution time: {} ms", 
                jobExecution.getEndTime().getTime() - jobExecution.getStartTime().getTime());
        }
    }
}

