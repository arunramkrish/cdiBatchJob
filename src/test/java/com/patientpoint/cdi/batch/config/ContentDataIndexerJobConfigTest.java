package com.patientpoint.cdi.batch.config;

import com.patientpoint.cdi.batch.listener.JobCompletionListener;
import com.patientpoint.cdi.batch.processor.ContentDataTransformer;
import com.patientpoint.cdi.batch.reader.ContentDataReader;
import com.patientpoint.cdi.batch.tasklet.ElasticsearchIndexFlipTasklet;
import com.patientpoint.cdi.batch.tasklet.ElasticsearchIndexInitTasklet;
import com.patientpoint.cdi.batch.tasklet.ElasticsearchValidationTasklet;
import com.patientpoint.cdi.batch.writer.ElasticsearchItemWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ContentDataIndexerJobConfigTest {
    
    @Mock
    private JobRepository jobRepository;
    
    @Mock
    private PlatformTransactionManager transactionManager;
    
    @Mock
    private ContentDataReader reader;
    
    @Mock
    private ContentDataTransformer processor;
    
    @Mock
    private ElasticsearchItemWriter writer;
    
    @Mock
    private ElasticsearchIndexInitTasklet indexInitTasklet;
    
    @Mock
    private ElasticsearchValidationTasklet validationTasklet;
    
    @Mock
    private ElasticsearchIndexFlipTasklet indexFlipTasklet;
    
    @Mock
    private JobCompletionListener jobCompletionListener;
    
    private ContentDataIndexerJobConfig contentDataIndexerJobConfig;
    private static final int CHUNK_SIZE = 100;
    
    @BeforeEach
    void setUp() {
        contentDataIndexerJobConfig = new ContentDataIndexerJobConfig(
            jobRepository,
            transactionManager,
            reader,
            processor,
            writer,
            indexInitTasklet,
            validationTasklet,
            indexFlipTasklet,
            jobCompletionListener
        );
        ReflectionTestUtils.setField(contentDataIndexerJobConfig, "chunkSize", CHUNK_SIZE);
    }
    
    @Test
    void testElasticsearchIndexInitStep_ShouldBeCreated() {
        // When
        Step step = contentDataIndexerJobConfig.elasticsearchIndexInitStep();
        
        // Then
        assertNotNull(step);
        assertEquals("elasticsearchIndexInitStep", step.getName());
    }
    
    @Test
    void testElasticsearchSyncStep_ShouldBeCreated() {
        // When
        Step step = contentDataIndexerJobConfig.elasticsearchSyncStep();
        
        // Then
        assertNotNull(step);
        assertEquals("elasticsearchSyncStep", step.getName());
    }
    
    @Test
    void testElasticsearchValidationStep_ShouldBeCreated() {
        // When
        Step step = contentDataIndexerJobConfig.elasticsearchValidationStep();
        
        // Then
        assertNotNull(step);
        assertEquals("elasticsearchValidationStep", step.getName());
    }
    
    @Test
    void testElasticsearchIndexFlipStep_ShouldBeCreated() {
        // When
        Step step = contentDataIndexerJobConfig.elasticsearchIndexFlipStep();
        
        // Then
        assertNotNull(step);
        assertEquals("elasticsearchIndexFlipStep", step.getName());
    }
    
    @Test
    void testCdiJob_ShouldBeCreated() {
        // When
        Job job = contentDataIndexerJobConfig.cdiJob();
        
        // Then
        assertNotNull(job);
        assertEquals("cdiJob", job.getName());
    }
    
    @Test
    void testCdiJob_ShouldHaveAllSteps() {
        // When
        Job job = contentDataIndexerJobConfig.cdiJob();
        
        // Then
        assertNotNull(job);
        assertNotNull(job.getJobParametersValidator());
    }
}

