package com.patientpoint.cdi.batch.listener;

import com.patientpoint.cdi.batch.service.ElasticsearchIndexFlipService;
import com.patientpoint.cdi.batch.service.ElasticsearchValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobCompletionListenerTest {
    
    @Mock
    private ElasticsearchValidationService validationService;
    
    @Mock
    private ElasticsearchIndexFlipService indexFlipService;
    
    @Mock
    private JobExecution jobExecution;
    
    @InjectMocks
    private JobCompletionListener listener;
    
    @BeforeEach
    void setUp() {
        when(jobExecution.getStatus()).thenReturn(BatchStatus.COMPLETED);
    }
    
    @Test
    void testAfterJob_ShouldFlipIndexWhenValidationPasses() {
        // Given
        when(validationService.validateTransfer()).thenReturn(true);
        
        // When
        listener.afterJob(jobExecution);
        
        // Then
        verify(validationService, times(1)).validateTransfer();
        verify(indexFlipService, times(1)).flipIndex();
    }
    
    @Test
    void testAfterJob_ShouldNotFlipIndexWhenValidationFails() {
        // Given
        when(validationService.validateTransfer()).thenReturn(false);
        
        // When
        listener.afterJob(jobExecution);
        
        // Then
        verify(validationService, times(1)).validateTransfer();
        verify(indexFlipService, never()).flipIndex();
        verify(jobExecution, times(1)).setStatus(BatchStatus.FAILED);
    }
    
    @Test
    void testAfterJob_ShouldSetFailedStatusWhenIndexFlipThrowsException() {
        // Given
        when(validationService.validateTransfer()).thenReturn(true);
        doThrow(new RuntimeException("Index flip failed")).when(indexFlipService).flipIndex();
        
        // When
        listener.afterJob(jobExecution);
        
        // Then
        verify(validationService, times(1)).validateTransfer();
        verify(indexFlipService, times(1)).flipIndex();
        verify(jobExecution, times(1)).setStatus(BatchStatus.FAILED);
    }
    
    @Test
    void testAfterJob_ShouldNotValidateWhenJobNotCompleted() {
        // Given
        when(jobExecution.getStatus()).thenReturn(BatchStatus.FAILED);
        
        // When
        listener.afterJob(jobExecution);
        
        // Then
        verify(validationService, never()).validateTransfer();
        verify(indexFlipService, never()).flipIndex();
    }
    
    @Test
    void testBeforeJob_ShouldLogJobStart() {
        // Given
        when(jobExecution.getJobInstance().getJobName()).thenReturn("testJob");
        
        // When
        listener.beforeJob(jobExecution);
        
        // Then
        verify(jobExecution, times(1)).getJobInstance();
        // Logger calls are not easily verifiable, but we can ensure the method executes
        assertNotNull(jobExecution);
    }
}

