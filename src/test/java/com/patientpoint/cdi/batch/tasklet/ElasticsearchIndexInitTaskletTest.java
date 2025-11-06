package com.patientpoint.cdi.batch.tasklet;

import com.patientpoint.cdi.batch.service.ElasticsearchIndexInitService;
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
class ElasticsearchIndexInitTaskletTest {
    
    @Mock
    private ElasticsearchIndexInitService indexInitService;
    
    @Mock
    private StepContribution stepContribution;
    
    @Mock
    private ChunkContext chunkContext;
    
    private ElasticsearchIndexInitTasklet tasklet;
    
    @BeforeEach
    void setUp() {
        tasklet = new ElasticsearchIndexInitTasklet(indexInitService);
    }
    
    @Test
    void testExecute_ShouldCallInitializeIndex() throws Exception {
        // Given
        doNothing().when(indexInitService).initializeIndex();
        
        // When
        RepeatStatus result = tasklet.execute(stepContribution, chunkContext);
        
        // Then
        assertEquals(RepeatStatus.FINISHED, result);
        verify(indexInitService, times(1)).initializeIndex();
    }
    
    @Test
    void testExecute_ShouldReturnFinishedStatus() throws Exception {
        // Given
        doNothing().when(indexInitService).initializeIndex();
        
        // When
        RepeatStatus result = tasklet.execute(stepContribution, chunkContext);
        
        // Then
        assertNotNull(result);
        assertEquals(RepeatStatus.FINISHED, result);
    }
    
    @Test
    void testExecute_ShouldPropagateExceptionWhenServiceThrows() throws Exception {
        // Given
        doThrow(new RuntimeException("Index initialization failed"))
            .when(indexInitService).initializeIndex();
        
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            tasklet.execute(stepContribution, chunkContext);
        });
        
        verify(indexInitService, times(1)).initializeIndex();
    }
}

