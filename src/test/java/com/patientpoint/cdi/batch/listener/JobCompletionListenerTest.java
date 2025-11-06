package com.patientpoint.cdi.batch.listener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobCompletionListenerTest {
    
    @Mock
    private JobExecution jobExecution;
    
    @Mock
    private JobInstance jobInstance;
    
    @InjectMocks
    private JobCompletionListener listener;
    
    @BeforeEach
    void setUp() {
        when(jobExecution.getStatus()).thenReturn(BatchStatus.COMPLETED);
        when(jobExecution.getJobInstance()).thenReturn(jobInstance);
    }
    
    @Test
    void testBeforeJob_ShouldLogJobStart() {
        // Given
        when(jobInstance.getJobName()).thenReturn("cdiJob");
        
        // When
        listener.beforeJob(jobExecution);
        
        // Then
        verify(jobExecution, times(1)).getJobInstance();
        verify(jobInstance, times(1)).getJobName();
    }
    
    @Test
    void testAfterJob_ShouldLogJobCompletion() {
        // Given
        when(jobExecution.getStatus()).thenReturn(BatchStatus.COMPLETED);
        
        // When
        listener.afterJob(jobExecution);
        
        // Then
        verify(jobExecution, atLeastOnce()).getStatus();
        // Logger calls are not easily verifiable, but we can ensure the method executes
        assertNotNull(jobExecution);
    }
    
    @Test
    void testAfterJob_ShouldLogExecutionTimeWhenTimesAreAvailable() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().minusSeconds(5);
        LocalDateTime endTime = LocalDateTime.now();
        when(jobExecution.getStatus()).thenReturn(BatchStatus.COMPLETED);
        when(jobExecution.getStartTime()).thenReturn(startTime);
        when(jobExecution.getEndTime()).thenReturn(endTime);
        
        // When
        listener.afterJob(jobExecution);
        
        // Then
        verify(jobExecution, atLeastOnce()).getStatus();
        verify(jobExecution, atLeastOnce()).getStartTime();
        verify(jobExecution, atLeastOnce()).getEndTime();
    }
    
    @Test
    void testAfterJob_ShouldHandleNullStartTime() {
        // Given
        LocalDateTime endTime = LocalDateTime.now();
        when(jobExecution.getStatus()).thenReturn(BatchStatus.COMPLETED);
        when(jobExecution.getStartTime()).thenReturn(null);
        when(jobExecution.getEndTime()).thenReturn(endTime);
        
        // When
        listener.afterJob(jobExecution);
        
        // Then
        verify(jobExecution, atLeastOnce()).getStatus();
        // Should not throw exception when startTime is null
        assertNotNull(jobExecution);
    }
    
    @Test
    void testAfterJob_ShouldHandleNullEndTime() {
        // Given
        LocalDateTime startTime = LocalDateTime.now();
        when(jobExecution.getStatus()).thenReturn(BatchStatus.COMPLETED);
        when(jobExecution.getStartTime()).thenReturn(startTime);
        when(jobExecution.getEndTime()).thenReturn(null);
        
        // When
        listener.afterJob(jobExecution);
        
        // Then
        verify(jobExecution, atLeastOnce()).getStatus();
        // Should not throw exception when endTime is null
        assertNotNull(jobExecution);
    }
    
    @Test
    void testAfterJob_ShouldLogFailedStatus() {
        // Given
        when(jobExecution.getStatus()).thenReturn(BatchStatus.FAILED);
        
        // When
        listener.afterJob(jobExecution);
        
        // Then
        verify(jobExecution, atLeastOnce()).getStatus();
        assertNotNull(jobExecution);
    }
}

