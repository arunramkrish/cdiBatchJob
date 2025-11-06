package com.patientpoint.cdi.batch.config;

import com.patientpoint.cdi.batch.listener.JobCompletionListener;
import com.patientpoint.cdi.batch.processor.ContentDataTransformer;
import com.patientpoint.cdi.batch.reader.ContentDataReader;
import com.patientpoint.cdi.batch.tasklet.ElasticsearchIndexFlipTasklet;
import com.patientpoint.cdi.batch.tasklet.ElasticsearchIndexInitTasklet;
import com.patientpoint.cdi.batch.tasklet.ElasticsearchValidationTasklet;
import com.patientpoint.cdi.batch.writer.ElasticsearchItemWriter;
import com.patientpoint.cdi.model.ElasticsearchContent;
import com.patientpoint.cdi.model.EditorialContent;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ContentDataIndexerJobConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ContentDataReader reader;
    private final ContentDataTransformer processor;
    private final ElasticsearchItemWriter writer;
    private final ElasticsearchIndexInitTasklet indexInitTasklet;
    private final ElasticsearchValidationTasklet validationTasklet;
    private final ElasticsearchIndexFlipTasklet indexFlipTasklet;
    private final JobCompletionListener jobCompletionListener;
    
    @Value("${batch.job.chunk-size:1000}")
    private int chunkSize;
    
    public ContentDataIndexerJobConfig(JobRepository jobRepository,
                         PlatformTransactionManager transactionManager,
                         ContentDataReader reader,
                         ContentDataTransformer processor,
                         ElasticsearchItemWriter writer,
                         ElasticsearchIndexInitTasklet indexInitTasklet,
                         ElasticsearchValidationTasklet validationTasklet,
                         ElasticsearchIndexFlipTasklet indexFlipTasklet,
                         JobCompletionListener jobCompletionListener) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.reader = reader;
        this.processor = processor;
        this.writer = writer;
        this.indexInitTasklet = indexInitTasklet;
        this.validationTasklet = validationTasklet;
        this.indexFlipTasklet = indexFlipTasklet;
        this.jobCompletionListener = jobCompletionListener;
    }
    
    @Bean
    public Step elasticsearchIndexInitStep() {
        return new StepBuilder("elasticsearchIndexInitStep", jobRepository)
            .tasklet(indexInitTasklet, transactionManager)
            .build();
    }
    
    @Bean
    public Step elasticsearchSyncStep() {
        return new StepBuilder("elasticsearchSyncStep", jobRepository)
            .<EditorialContent, ElasticsearchContent>chunk(chunkSize, transactionManager)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .build();
    }
    
    @Bean
    public Step elasticsearchValidationStep() {
        return new StepBuilder("elasticsearchValidationStep", jobRepository)
            .tasklet(validationTasklet, transactionManager)
            .build();
    }
    
    @Bean
    public Step elasticsearchIndexFlipStep() {
        return new StepBuilder("elasticsearchIndexFlipStep", jobRepository)
            .tasklet(indexFlipTasklet, transactionManager)
            .build();
    }
    
    @Bean
    public Job elasticsearchSyncJob() {
        return new JobBuilder("elasticsearchSyncJob", jobRepository)
            .start(elasticsearchIndexInitStep())
            .next(elasticsearchSyncStep())
            .next(elasticsearchValidationStep())
            .next(elasticsearchIndexFlipStep())
            .listener(jobCompletionListener)
            .build();
    }
}

