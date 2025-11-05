package com.patientpoint.cdi.batch.processor;

import com.patientpoint.cdi.model.ElasticsearchContent;
import com.patientpoint.cdi.model.EditorialContent;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MongoToElasticsearchProcessor implements ItemProcessor<EditorialContent, ElasticsearchContent> {
    
    @Override
    public ElasticsearchContent process(EditorialContent editorialContent) throws Exception {
        ElasticsearchContent elasticsearchContent = new ElasticsearchContent();
        
        // Convert UUID to String for Elasticsearch
        elasticsearchContent.setId(editorialContent.getId() != null ? editorialContent.getId().toString() : null);
        elasticsearchContent.setTitle(editorialContent.getTitle());
        elasticsearchContent.setContent(editorialContent.getContent());
        elasticsearchContent.setMetadata(editorialContent.getMetadata());
        elasticsearchContent.setCreatedAt(editorialContent.getCreatedAt());
        elasticsearchContent.setUpdatedAt(editorialContent.getUpdatedAt());
        elasticsearchContent.setStatus(editorialContent.getStatus());
        elasticsearchContent.setIndexedAt(LocalDateTime.now());
        
        return elasticsearchContent;
    }
}

