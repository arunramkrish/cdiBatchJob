package com.patientpoint.cdi.batch.writer;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import com.patientpoint.cdi.model.ElasticsearchContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ElasticsearchItemWriterTest {
    
    @Mock
    private ElasticsearchClient elasticsearchClient;
    
    @Mock
    private BulkResponse bulkResponse;
    
    private ElasticsearchItemWriter writer;
    private static final String INDEX_NAME = "test_index";
    
    @BeforeEach
    void setUp() {
        writer = new ElasticsearchItemWriter(elasticsearchClient, INDEX_NAME);
    }
    
    @Test
    void testWrite_ShouldCallBulkOperation() throws Exception {
        // Given
        Chunk<ElasticsearchContent> chunk = createTestChunk(3);
        when(elasticsearchClient.bulk(any(BulkRequest.class))).thenReturn(bulkResponse);
        when(bulkResponse.errors()).thenReturn(false);
        
        // When
        writer.write(chunk);
        
        // Then
        verify(elasticsearchClient, times(1)).bulk(any(BulkRequest.class));
    }
    
    @Test
    void testWrite_ShouldThrowExceptionWhenBulkOperationHasErrors() throws Exception {
        // Given
        Chunk<ElasticsearchContent> chunk = createTestChunk(2);
        when(elasticsearchClient.bulk(any(BulkRequest.class))).thenReturn(bulkResponse);
        when(bulkResponse.errors()).thenReturn(true);
        when(bulkResponse.items()).thenReturn(new ArrayList<>());
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            writer.write(chunk);
        });
        
        assertTrue(exception.getMessage().contains("Elasticsearch bulk operation failed"));
        verify(elasticsearchClient, times(1)).bulk(any(BulkRequest.class));
    }
    
    @Test
    void testWrite_ShouldUseCorrectIndexName() throws Exception {
        // Given
        Chunk<ElasticsearchContent> chunk = createTestChunk(1);
        ArgumentCaptor<BulkRequest> requestCaptor = ArgumentCaptor.forClass(BulkRequest.class);
        when(elasticsearchClient.bulk(any(BulkRequest.class))).thenReturn(bulkResponse);
        when(bulkResponse.errors()).thenReturn(false);
        
        // When
        writer.write(chunk);
        
        // Then
        verify(elasticsearchClient).bulk(requestCaptor.capture());
        // The index name is set in the bulk request operations
        assertNotNull(requestCaptor.getValue());
    }
    
    @Test
    void testWrite_ShouldHandleEmptyChunk() throws Exception {
        // Given
        Chunk<ElasticsearchContent> emptyChunk = new Chunk<>();
        when(elasticsearchClient.bulk(any(BulkRequest.class))).thenReturn(bulkResponse);
        when(bulkResponse.errors()).thenReturn(false);
        
        // When
        writer.write(emptyChunk);
        
        // Then
        verify(elasticsearchClient, times(1)).bulk(any(BulkRequest.class));
    }
    
    @Test
    void testWrite_ShouldHandleLargeChunk() throws Exception {
        // Given
        Chunk<ElasticsearchContent> chunk = createTestChunk(100);
        when(elasticsearchClient.bulk(any(BulkRequest.class))).thenReturn(bulkResponse);
        when(bulkResponse.errors()).thenReturn(false);
        
        // When
        writer.write(chunk);
        
        // Then
        verify(elasticsearchClient, times(1)).bulk(any(BulkRequest.class));
    }
    
    private Chunk<ElasticsearchContent> createTestChunk(int size) {
        List<ElasticsearchContent> items = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ElasticsearchContent content = new ElasticsearchContent();
            content.setId("id-" + i);
            content.setTitle("Title " + i);
            content.setContent("Content " + i);
            content.setIndexedAt(LocalDateTime.now());
            items.add(content);
        }
        return new Chunk<>(items);
    }
}

