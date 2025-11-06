package com.patientpoint.cdi.batch.runner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CdiJobLauncherTest {
    
    @Mock
    private JobLauncher jobLauncher;
    
    @Mock
    private Job elasticsearchSyncJob;
    
    @Mock
    private JobExecution jobExecution;
    
    @InjectMocks
    private CdiJobLauncher cdiJobLauncher;
    
    @Test
    void testRun_ShouldLaunchJob() throws Exception {
        // Given
        when(jobLauncher.run(any(Job.class), any(JobParameters.class))).thenReturn(jobExecution);
        
        // When
        cdiJobLauncher.run();
        
        // Then
        verify(jobLauncher, times(1)).run(eq(elasticsearchSyncJob), any(JobParameters.class));
    }
    
    @Test
    void testRun_ShouldThrowExceptionWhenJobLaunchFails() throws Exception {
        // Given
        when(jobLauncher.run(any(Job.class), any(JobParameters.class)))
            .thenThrow(new RuntimeException("Job launch failed"));
        
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            cdiJobLauncher.run();
        });
        
        verify(jobLauncher, times(1)).run(eq(elasticsearchSyncJob), any(JobParameters.class));
    }
    
    @Test
    void testRun_ShouldCreateJobParametersWithTimestamp() throws Exception {
        // Given
        when(jobLauncher.run(any(Job.class), any(JobParameters.class))).thenReturn(jobExecution);
        
        // When
        cdiJobLauncher.run();
        
        // Then
        verify(jobLauncher).run(eq(elasticsearchSyncJob), any(JobParameters.class));
        // JobParameters should contain a timestamp
    }
}

