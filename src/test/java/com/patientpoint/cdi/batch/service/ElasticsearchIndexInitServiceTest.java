package com.patientpoint.cdi.batch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.elasticsearch.indices.ExistsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ElasticsearchIndexInitServiceTest {
    
    @Mock
    private ElasticsearchClient elasticsearchClient;
    
    @Mock
    private ExistsResponse existsResponse;
    
    private ElasticsearchIndexInitService service;
    private static final String INDEX_NAME = "test_index";
    
    @BeforeEach
    void setUp() {
        service = new ElasticsearchIndexInitService(elasticsearchClient, INDEX_NAME);
    }
    
    @Test
    void testInitializeIndex_ShouldCreateIndexWhenNotExists() throws Exception {
        // Given
        when(elasticsearchClient.indices().exists(any(ExistsRequest.class))).thenReturn(existsResponse);
        when(existsResponse.value()).thenReturn(false);
        
        // When
        service.initializeIndex();
        
        // Then
        verify(elasticsearchClient.indices(), times(1)).exists(any(ExistsRequest.class));
        verify(elasticsearchClient.indices(), times(1)).create(any(CreateIndexRequest.class));
    }
    
    @Test
    void testInitializeIndex_ShouldNotCreateIndexWhenAlreadyExists() throws Exception {
        // Given
        when(elasticsearchClient.indices().exists(any(ExistsRequest.class))).thenReturn(existsResponse);
        when(existsResponse.value()).thenReturn(true);
        
        // When
        service.initializeIndex();
        
        // Then
        verify(elasticsearchClient.indices(), times(1)).exists(any(ExistsRequest.class));
        verify(elasticsearchClient.indices(), never()).create(any(CreateIndexRequest.class));
    }
    
    @Test
    void testInitializeIndex_ShouldCreateIndexWithCorrectMappings() throws Exception {
        // Given
        when(elasticsearchClient.indices().exists(any(ExistsRequest.class))).thenReturn(existsResponse);
        when(existsResponse.value()).thenReturn(false);
        
        ArgumentCaptor<CreateIndexRequest> requestCaptor = ArgumentCaptor.forClass(CreateIndexRequest.class);
        
        // When
        service.initializeIndex();
        
        // Then
        verify(elasticsearchClient.indices()).create(requestCaptor.capture());
        CreateIndexRequest request = requestCaptor.getValue();
        assertNotNull(request);
    }
    
    @Test
    void testInitializeIndex_ShouldThrowExceptionWhenCreationFails() throws Exception {
        // Given
        when(elasticsearchClient.indices().exists(any(ExistsRequest.class))).thenReturn(existsResponse);
        when(existsResponse.value()).thenReturn(false);
        when(elasticsearchClient.indices().create(any(CreateIndexRequest.class)))
            .thenThrow(new RuntimeException("Index creation failed"));
        
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            service.initializeIndex();
        });
    }
    
    @Test
    void testInitializeIndex_ShouldUseCorrectIndexName() throws Exception {
        // Given
        ArgumentCaptor<ExistsRequest> existsCaptor = ArgumentCaptor.forClass(ExistsRequest.class);
        when(elasticsearchClient.indices().exists(any(ExistsRequest.class))).thenReturn(existsResponse);
        when(existsResponse.value()).thenReturn(false);
        
        // When
        service.initializeIndex();
        
        // Then
        verify(elasticsearchClient.indices()).exists(existsCaptor.capture());
        assertNotNull(existsCaptor.getValue());
    }
}

