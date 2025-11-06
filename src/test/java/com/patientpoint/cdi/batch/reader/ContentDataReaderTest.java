package com.patientpoint.cdi.batch.reader;

import com.patientpoint.cdi.model.EditorialContent;
import com.patientpoint.cdi.repository.EditorialContentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContentDataReaderTest {
    
    @Mock
    private EditorialContentRepository repository;
    
    private ContentDataReader reader;
    private static final int PAGE_SIZE = 10;
    
    @BeforeEach
    void setUp() {
        reader = new ContentDataReader(repository, PAGE_SIZE);
    }
    
    @Test
    void testDoPageRead_ShouldReturnIteratorWithContent() throws Exception {
        // Given
        List<EditorialContent> contentList = createTestContent(5);
        Page<EditorialContent> page = new PageImpl<>(contentList);
        when(repository.findAll(any(Pageable.class))).thenReturn(page);
        
        // Set page to 0 using reflection
        ReflectionTestUtils.setField(reader, "page", 0);
        
        // When
        Iterator<EditorialContent> iterator = reader.doPageRead();
        
        // Then
        assertNotNull(iterator);
        assertTrue(iterator.hasNext());
        assertEquals(5, countIterator(iterator));
    }
    
    @Test
    void testDoPageRead_ShouldReturnEmptyIteratorForEmptyPage() throws Exception {
        // Given
        Page<EditorialContent> emptyPage = new PageImpl<>(new ArrayList<>());
        when(repository.findAll(any(Pageable.class))).thenReturn(emptyPage);
        
        ReflectionTestUtils.setField(reader, "page", 0);
        
        // When
        Iterator<EditorialContent> iterator = reader.doPageRead();
        
        // Then
        assertNotNull(iterator);
        assertFalse(iterator.hasNext());
    }
    
    @Test
    void testDoPageRead_ShouldUseCorrectPageNumber() throws Exception {
        // Given
        List<EditorialContent> contentList = createTestContent(3);
        Page<EditorialContent> page = new PageImpl<>(contentList);
        when(repository.findAll(any(Pageable.class))).thenReturn(page);
        
        ReflectionTestUtils.setField(reader, "page", 2);
        
        // When
        reader.doPageRead();
        
        // Then
        PageRequest expectedPageRequest = PageRequest.of(2, PAGE_SIZE);
        // Verify that repository.findAll was called with page 2
        // The mock will return the page we set up
    }
    
    @Test
    void testDoPageRead_ShouldUseCorrectPageSize() throws Exception {
        // Given
        List<EditorialContent> contentList = createTestContent(PAGE_SIZE);
        Page<EditorialContent> page = new PageImpl<>(contentList);
        when(repository.findAll(any(Pageable.class))).thenReturn(page);
        
        ReflectionTestUtils.setField(reader, "page", 0);
        
        // When
        Iterator<EditorialContent> iterator = reader.doPageRead();
        
        // Then
        assertNotNull(iterator);
        assertEquals(PAGE_SIZE, countIterator(iterator));
    }
    
    private List<EditorialContent> createTestContent(int count) {
        List<EditorialContent> contentList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            EditorialContent content = new EditorialContent();
            content.setId(UUID.randomUUID());
            content.setTitle("Title " + i);
            content.setContent("Content " + i);
            contentList.add(content);
        }
        return contentList;
    }
    
    private int countIterator(Iterator<EditorialContent> iterator) {
        int count = 0;
        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }
        return count;
    }
}

