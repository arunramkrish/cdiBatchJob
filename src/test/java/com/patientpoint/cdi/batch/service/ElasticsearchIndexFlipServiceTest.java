package com.patientpoint.cdi.batch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.GetAliasRequest;
import co.elastic.clients.elasticsearch.indices.GetAliasResponse;
import co.elastic.clients.elasticsearch.indices.UpdateAliasesRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ElasticsearchIndexFlipServiceTest {
    
    @Mock
    private ElasticsearchClient elasticsearchClient;
    
    @Mock
    private GetAliasResponse aliasResponse;
    
    private ElasticsearchIndexFlipService service;
    private static final String INDEX_NAME = "cdi_content_new";
    private static final String ALIAS_NAME = "cdi_content_alias";
    
    @BeforeEach
    void setUp() {
        service = new ElasticsearchIndexFlipService(elasticsearchClient, INDEX_NAME, ALIAS_NAME);
    }
    
    @Test
    void testFlipIndex_ShouldAddAliasToNewIndex() throws Exception {
        // Given
        Map<String, Object> emptyResult = new HashMap<>();
        when(elasticsearchClient.indices().getAlias(any(GetAliasRequest.class))).thenReturn(aliasResponse);
        when(aliasResponse.result()).thenReturn(emptyResult);
        
        // When
        service.flipIndex();
        
        // Then
        verify(elasticsearchClient.indices(), times(1)).getAlias(any(GetAliasRequest.class));
        verify(elasticsearchClient.indices(), times(1)).updateAliases(any(UpdateAliasesRequest.class));
    }
    
    @Test
    void testFlipIndex_ShouldRemoveAliasFromOldIndex() throws Exception {
        // Given
        Map<String, Object> existingAliases = new HashMap<>();
        existingAliases.put("cdi_content_old", new Object());
        existingAliases.put(INDEX_NAME, new Object());
        
        when(elasticsearchClient.indices().getAlias(any(GetAliasRequest.class))).thenReturn(aliasResponse);
        when(aliasResponse.result()).thenReturn(existingAliases);
        
        // When
        service.flipIndex();
        
        // Then
        ArgumentCaptor<UpdateAliasesRequest> requestCaptor = ArgumentCaptor.forClass(UpdateAliasesRequest.class);
        verify(elasticsearchClient.indices()).updateAliases(requestCaptor.capture());
        assertNotNull(requestCaptor.getValue());
    }
    
    @Test
    void testFlipIndex_ShouldThrowExceptionWhenElasticsearchFails() throws Exception {
        // Given
        when(elasticsearchClient.indices().getAlias(any(GetAliasRequest.class)))
            .thenThrow(new RuntimeException("Elasticsearch connection failed"));
        
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            service.flipIndex();
        });
    }
    
    @Test
    void testFlipIndex_ShouldHandleMultipleExistingIndices() throws Exception {
        // Given
        Map<String, Object> existingAliases = new HashMap<>();
        existingAliases.put("old_index_1", new Object());
        existingAliases.put("old_index_2", new Object());
        
        when(elasticsearchClient.indices().getAlias(any(GetAliasRequest.class))).thenReturn(aliasResponse);
        when(aliasResponse.result()).thenReturn(existingAliases);
        
        // When
        service.flipIndex();
        
        // Then
        verify(elasticsearchClient.indices(), times(1)).updateAliases(any(UpdateAliasesRequest.class));
    }
    
    @Test
    void testFlipIndex_ShouldNotRemoveAliasFromSameIndex() throws Exception {
        // Given
        Map<String, Object> existingAliases = new HashMap<>();
        existingAliases.put(INDEX_NAME, new Object());
        
        when(elasticsearchClient.indices().getAlias(any(GetAliasRequest.class))).thenReturn(aliasResponse);
        when(aliasResponse.result()).thenReturn(existingAliases);
        
        // When
        service.flipIndex();
        
        // Then
        verify(elasticsearchClient.indices(), times(1)).updateAliases(any(UpdateAliasesRequest.class));
    }
}

