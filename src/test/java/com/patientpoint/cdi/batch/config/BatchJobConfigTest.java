package com.patientpoint.cdi.batch.config;

import com.patientpoint.cdi.batch.listener.JobCompletionListener;
import com.patientpoint.cdi.batch.processor.MongoToElasticsearchProcessor;
import com.patientpoint.cdi.batch.reader.MongoContentItemReader;
import com.patientpoint.cdi.batch.writer.ElasticsearchItemWriter;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.PlatformTransactionManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {BatchJobConfig.class})
@TestPropertySource(properties = {
    "batch.job.chunk-size=100",
    "elasticsearch.index.name=test_index"
})
class BatchJobConfigTest {
    
    @Autowired
    private BatchJobConfig batchJobConfig;
    
    @MockBean
    private JobRepository jobRepository;
    
    @MockBean
    private PlatformTransactionManager transactionManager;
    
    @MockBean
    private MongoContentItemReader reader;
    
    @MockBean
    private MongoToElasticsearchProcessor processor;
    
    @MockBean
    private ElasticsearchItemWriter writer;
    
    @MockBean
    private JobCompletionListener jobCompletionListener;
    
    @Test
    void testElasticsearchSyncStep_ShouldBeCreated() {
        // When
        Step step = batchJobConfig.elasticsearchSyncStep();
        
        // Then
        assertNotNull(step);
        assertEquals("elasticsearchSyncStep", step.getName());
    }
    
    @Test
    void testElasticsearchSyncJob_ShouldBeCreated() {
        // When
        Job job = batchJobConfig.elasticsearchSyncJob();
        
        // Then
        assertNotNull(job);
        assertEquals("elasticsearchSyncJob", job.getName());
    }
    
    @Test
    void testElasticsearchSyncJob_ShouldHaveStep() {
        // When
        Job job = batchJobConfig.elasticsearchSyncJob();
        
        // Then
        assertNotNull(job);
        // Job should have at least one step
        assertNotNull(job.getJobParametersValidator());
    }
}

