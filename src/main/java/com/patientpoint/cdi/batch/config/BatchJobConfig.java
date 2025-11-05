package com.patientpoint.cdi.batch.config;

import com.patientpoint.cdi.batch.listener.JobCompletionListener;
import com.patientpoint.cdi.batch.processor.MongoToElasticsearchProcessor;
import com.patientpoint.cdi.batch.reader.MongoContentItemReader;
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
public class BatchJobConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final MongoContentItemReader reader;
    private final MongoToElasticsearchProcessor processor;
    private final ElasticsearchItemWriter writer;
    private final JobCompletionListener jobCompletionListener;
    
    @Value("${batch.job.chunk-size:1000}")
    private int chunkSize;
    
    public BatchJobConfig(JobRepository jobRepository,
                         PlatformTransactionManager transactionManager,
                         MongoContentItemReader reader,
                         MongoToElasticsearchProcessor processor,
                         ElasticsearchItemWriter writer,
                         JobCompletionListener jobCompletionListener) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.reader = reader;
        this.processor = processor;
        this.writer = writer;
        this.jobCompletionListener = jobCompletionListener;
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
    public Job elasticsearchSyncJob() {
        return new JobBuilder("elasticsearchSyncJob", jobRepository)
            .start(elasticsearchSyncStep())
            .listener(jobCompletionListener)
            .build();
    }
}

