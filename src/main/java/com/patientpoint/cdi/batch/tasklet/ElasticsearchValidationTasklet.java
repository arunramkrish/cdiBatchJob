package com.patientpoint.cdi.batch.tasklet;

import com.patientpoint.cdi.batch.service.ElasticsearchValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class ElasticsearchValidationTasklet implements Tasklet {
    
    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchValidationTasklet.class);
    
    private final ElasticsearchValidationService validationService;
    
    public ElasticsearchValidationTasklet(ElasticsearchValidationService validationService) {
        this.validationService = validationService;
    }
    
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        logger.info("Starting validation step...");
        
        boolean isValid = validationService.validateTransfer();
        
        if (!isValid) {
            logger.error("Validation failed. Job will be marked as failed.");
            throw new RuntimeException("Validation failed: MongoDB count does not match Elasticsearch count");
        }
        
        logger.info("Validation passed successfully.");
        return RepeatStatus.FINISHED;
    }
}

