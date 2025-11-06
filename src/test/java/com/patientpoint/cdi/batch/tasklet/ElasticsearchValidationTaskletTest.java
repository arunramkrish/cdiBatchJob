package com.patientpoint.cdi.batch.tasklet;

import com.patientpoint.cdi.batch.service.ElasticsearchValidationService;
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
class ElasticsearchValidationTaskletTest {
    
    @Mock
    private ElasticsearchValidationService validationService;
    
    @Mock
    private StepContribution stepContribution;
    
    @Mock
    private ChunkContext chunkContext;
    
    private ElasticsearchValidationTasklet tasklet;
    
    @BeforeEach
    void setUp() {
        tasklet = new ElasticsearchValidationTasklet(validationService);
    }
    
    @Test
    void testExecute_ShouldReturnFinishedWhenValidationPasses() throws Exception {
        // Given
        when(validationService.validateTransfer()).thenReturn(true);
        
        // When
        RepeatStatus result = tasklet.execute(stepContribution, chunkContext);
        
        // Then
        assertEquals(RepeatStatus.FINISHED, result);
        verify(validationService, times(1)).validateTransfer();
    }
    
    @Test
    void testExecute_ShouldThrowExceptionWhenValidationFails() throws Exception {
        // Given
        when(validationService.validateTransfer()).thenReturn(false);
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tasklet.execute(stepContribution, chunkContext);
        });
        
        assertTrue(exception.getMessage().contains("Validation failed"));
        assertTrue(exception.getMessage().contains("MongoDB count does not match Elasticsearch count"));
        verify(validationService, times(1)).validateTransfer();
    }
    
    @Test
    void testExecute_ShouldPropagateExceptionWhenServiceThrows() throws Exception {
        // Given
        when(validationService.validateTransfer())
            .thenThrow(new RuntimeException("Validation service error"));
        
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            tasklet.execute(stepContribution, chunkContext);
        });
        
        verify(validationService, times(1)).validateTransfer();
    }
    
    @Test
    void testExecute_ShouldCallValidateTransfer() throws Exception {
        // Given
        when(validationService.validateTransfer()).thenReturn(true);
        
        // When
        tasklet.execute(stepContribution, chunkContext);
        
        // Then
        verify(validationService, times(1)).validateTransfer();
    }
}

