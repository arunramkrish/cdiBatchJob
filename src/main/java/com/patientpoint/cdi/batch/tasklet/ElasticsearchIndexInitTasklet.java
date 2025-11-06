package com.patientpoint.cdi.batch.tasklet;

import com.patientpoint.cdi.batch.service.ElasticsearchIndexInitService;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class ElasticsearchIndexInitTasklet implements Tasklet {
    
    private final ElasticsearchIndexInitService indexInitService;
    
    public ElasticsearchIndexInitTasklet(ElasticsearchIndexInitService indexInitService) {
        this.indexInitService = indexInitService;
    }
    
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        indexInitService.initializeIndex();
        return RepeatStatus.FINISHED;
    }
}

