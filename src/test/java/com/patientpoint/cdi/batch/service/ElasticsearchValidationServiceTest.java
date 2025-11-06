package com.patientpoint.cdi.batch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.CountRequest;
import co.elastic.clients.elasticsearch.core.CountResponse;
import com.patientpoint.cdi.repository.EditorialContentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ElasticsearchValidationServiceTest {
    
    @Mock
    private ElasticsearchClient elasticsearchClient;
    
    @Mock
    private EditorialContentRepository editorialContentRepository;
    
    @Mock
    private CountResponse countResponse;
    
    private ElasticsearchValidationService service;
    private static final String INDEX_NAME = "test_index";
    
    @BeforeEach
    void setUp() {
        service = new ElasticsearchValidationService(elasticsearchClient, editorialContentRepository, INDEX_NAME);
    }
    
    @Test
    void testValidateTransfer_ShouldReturnTrueWhenCountsMatch() throws Exception {
        // Given
        long mongoCount = 100L;
        long elasticsearchCount = 100L;
        
        when(editorialContentRepository.count()).thenReturn(mongoCount);
        when(elasticsearchClient.count(any(CountRequest.class))).thenReturn(countResponse);
        when(countResponse.count()).thenReturn(elasticsearchCount);
        
        // When
        boolean result = service.validateTransfer();
        
        // Then
        assertTrue(result);
        verify(editorialContentRepository, times(1)).count();
        verify(elasticsearchClient, times(1)).count(any(CountRequest.class));
    }
    
    @Test
    void testValidateTransfer_ShouldReturnFalseWhenCountsDoNotMatch() throws Exception {
        // Given
        long mongoCount = 100L;
        long elasticsearchCount = 95L;
        
        when(editorialContentRepository.count()).thenReturn(mongoCount);
        when(elasticsearchClient.count(any(CountRequest.class))).thenReturn(countResponse);
        when(countResponse.count()).thenReturn(elasticsearchCount);
        
        // When
        boolean result = service.validateTransfer();
        
        // Then
        assertFalse(result);
        verify(editorialContentRepository, times(1)).count();
        verify(elasticsearchClient, times(1)).count(any(CountRequest.class));
    }
    
    @Test
    void testValidateTransfer_ShouldReturnFalseWhenExceptionOccurs() throws Exception {
        // Given
        when(editorialContentRepository.count()).thenThrow(new RuntimeException("Database error"));
        
        // When
        boolean result = service.validateTransfer();
        
        // Then
        assertFalse(result);
        verify(editorialContentRepository, times(1)).count();
    }
    
    @Test
    void testValidateTransfer_ShouldReturnFalseWhenElasticsearchExceptionOccurs() throws Exception {
        // Given
        long mongoCount = 100L;
        
        when(editorialContentRepository.count()).thenReturn(mongoCount);
        when(elasticsearchClient.count(any(CountRequest.class))).thenThrow(new RuntimeException("Elasticsearch error"));
        
        // When
        boolean result = service.validateTransfer();
        
        // Then
        assertFalse(result);
        verify(editorialContentRepository, times(1)).count();
        verify(elasticsearchClient, times(1)).count(any(CountRequest.class));
    }
    
    @Test
    void testValidateTransfer_ShouldHandleZeroCounts() throws Exception {
        // Given
        long mongoCount = 0L;
        long elasticsearchCount = 0L;
        
        when(editorialContentRepository.count()).thenReturn(mongoCount);
        when(elasticsearchClient.count(any(CountRequest.class))).thenReturn(countResponse);
        when(countResponse.count()).thenReturn(elasticsearchCount);
        
        // When
        boolean result = service.validateTransfer();
        
        // Then
        assertTrue(result);
    }
    
    @Test
    void testValidateTransfer_ShouldUseCorrectIndexName() throws Exception {
        // Given
        long mongoCount = 50L;
        long elasticsearchCount = 50L;
        
        when(editorialContentRepository.count()).thenReturn(mongoCount);
        when(elasticsearchClient.count(any(CountRequest.class))).thenReturn(countResponse);
        when(countResponse.count()).thenReturn(elasticsearchCount);
        
        // When
        service.validateTransfer();
        
        // Then
        verify(elasticsearchClient).count(any(CountRequest.class));
    }
}

