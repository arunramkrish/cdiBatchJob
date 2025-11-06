package com.patientpoint.cdi.batch.tasklet;

import com.patientpoint.cdi.batch.service.ElasticsearchIndexFlipService;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class ElasticsearchIndexFlipTasklet implements Tasklet {
    
    private final ElasticsearchIndexFlipService indexFlipService;
    
    public ElasticsearchIndexFlipTasklet(ElasticsearchIndexFlipService indexFlipService) {
        this.indexFlipService = indexFlipService;
    }
    
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        indexFlipService.flipIndex();
        return RepeatStatus.FINISHED;
    }
}

