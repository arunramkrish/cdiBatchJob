package com.patientpoint.cdi.batch.tasklet;

import com.patientpoint.cdi.batch.service.ElasticsearchIndexFlipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ElasticsearchIndexFlipTaskletTest {
    
    @Mock
    private ElasticsearchIndexFlipService indexFlipService;
    
    @Mock
    private StepContribution stepContribution;
    
    @Mock
    private ChunkContext chunkContext;
    
    private ElasticsearchIndexFlipTasklet tasklet;
    
    @BeforeEach
    void setUp() {
        tasklet = new ElasticsearchIndexFlipTasklet(indexFlipService);
    }
    
    @Test
    void testExecute_ShouldCallFlipIndex() throws Exception {
        // Given
        doNothing().when(indexFlipService).flipIndex();
        
        // When
        RepeatStatus result = tasklet.execute(stepContribution, chunkContext);
        
        // Then
        assertEquals(RepeatStatus.FINISHED, result);
        verify(indexFlipService, times(1)).flipIndex();
    }
    
    @Test
    void testExecute_ShouldReturnFinishedStatus() throws Exception {
        // Given
        doNothing().when(indexFlipService).flipIndex();
        
        // When
        RepeatStatus result = tasklet.execute(stepContribution, chunkContext);
        
        // Then
        assertNotNull(result);
        assertEquals(RepeatStatus.FINISHED, result);
    }
    
    @Test
    void testExecute_ShouldPropagateExceptionWhenServiceThrows() throws Exception {
        // Given
        doThrow(new RuntimeException("Index flip failed"))
            .when(indexFlipService).flipIndex();
        
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            tasklet.execute(stepContribution, chunkContext);
        });
        
        verify(indexFlipService, times(1)).flipIndex();
    }
}

