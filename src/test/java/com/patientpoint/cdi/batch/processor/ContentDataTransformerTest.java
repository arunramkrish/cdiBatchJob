package com.patientpoint.cdi.batch.processor;

import com.patientpoint.cdi.model.ElasticsearchContent;
import com.patientpoint.cdi.model.EditorialContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ContentDataTransformerTest {
    
    @InjectMocks
    private ContentDataTransformer transformer;
    
    private EditorialContent editorialContent;
    private UUID testId;
    
    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        editorialContent = new EditorialContent();
        editorialContent.setId(testId);
        editorialContent.setTitle("Test Title");
        editorialContent.setContent("Test Content");
        editorialContent.setStatus("ACTIVE");
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("author", "Test Author");
        metadata.put("category", "Test Category");
        editorialContent.setMetadata(metadata);
        
        LocalDateTime now = LocalDateTime.now();
        editorialContent.setCreatedAt(now);
        editorialContent.setUpdatedAt(now);
    }
    
    @Test
    void testProcess_ShouldTransformEditorialContentToElasticsearchContent() throws Exception {
        // When
        ElasticsearchContent result = transformer.process(editorialContent);
        
        // Then
        assertNotNull(result);
        assertEquals(testId.toString(), result.getId());
        assertEquals("Test Title", result.getTitle());
        assertEquals("Test Content", result.getContent());
        assertEquals("ACTIVE", result.getStatus());
        assertEquals(editorialContent.getCreatedAt(), result.getCreatedAt());
        assertEquals(editorialContent.getUpdatedAt(), result.getUpdatedAt());
        assertEquals(editorialContent.getMetadata(), result.getMetadata());
        assertNotNull(result.getIndexedAt());
    }
    
    @Test
    void testProcess_ShouldHandleNullId() throws Exception {
        // Given
        editorialContent.setId(null);
        
        // When
        ElasticsearchContent result = transformer.process(editorialContent);
        
        // Then
        assertNotNull(result);
        assertNull(result.getId());
    }
    
    @Test
    void testProcess_ShouldSetIndexedAtTimestamp() throws Exception {
        // When
        ElasticsearchContent result = transformer.process(editorialContent);
        
        // Then
        assertNotNull(result.getIndexedAt());
        assertTrue(result.getIndexedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(result.getIndexedAt().isAfter(LocalDateTime.now().minusSeconds(1)));
    }
    
    @Test
    void testProcess_ShouldPreserveAllFields() throws Exception {
        // Given
        editorialContent.setTitle("Another Title");
        editorialContent.setContent("Another Content");
        editorialContent.setStatus("INACTIVE");
        
        // When
        ElasticsearchContent result = transformer.process(editorialContent);
        
        // Then
        assertEquals("Another Title", result.getTitle());
        assertEquals("Another Content", result.getContent());
        assertEquals("INACTIVE", result.getStatus());
    }
}

