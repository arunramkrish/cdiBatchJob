package com.patientpoint.cdi.batch.listener;

import com.patientpoint.cdi.batch.service.ElasticsearchIndexFlipService;
import com.patientpoint.cdi.batch.service.ElasticsearchValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionListener implements JobExecutionListener {
    
    private static final Logger logger = LoggerFactory.getLogger(JobCompletionListener.class);
    
    @Autowired
    private ElasticsearchValidationService validationService;
    
    @Autowired
    private ElasticsearchIndexFlipService indexFlipService;
    
    @Override
    public void beforeJob(JobExecution jobExecution) {
        logger.info("Starting Elasticsearch sync job: {}", jobExecution.getJobInstance().getJobName());
    }
    
    @Override
    public void afterJob(JobExecution jobExecution) {
        BatchStatus status = jobExecution.getStatus();
        logger.info("Job completed with status: {}", status);
        
        if (status == BatchStatus.COMPLETED) {
            logger.info("Job completed successfully. Starting validation...");
            
            boolean isValid = validationService.validateTransfer();
            
            if (isValid) {
                logger.info("Validation passed. Proceeding with index flip...");
                try {
                    indexFlipService.flipIndex();
                    logger.info("Index flip completed successfully");
                } catch (Exception e) {
                    logger.error("Failed to flip index", e);
                    jobExecution.setStatus(BatchStatus.FAILED);
                }
            } else {
                logger.error("Validation failed. Index flip will not be performed.");
                jobExecution.setStatus(BatchStatus.FAILED);
            }
        } else {
            logger.error("Job did not complete successfully. Status: {}", status);
        }
    }
}

