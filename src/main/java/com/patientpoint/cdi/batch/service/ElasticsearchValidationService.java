package com.patientpoint.cdi.batch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.CountRequest;
import com.patientpoint.cdi.repository.MongoContentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ElasticsearchValidationService {
    
    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchValidationService.class);
    
    private final ElasticsearchClient elasticsearchClient;
    private final MongoContentRepository mongoContentRepository;
    private final String indexName;
    
    public ElasticsearchValidationService(ElasticsearchClient elasticsearchClient,
                                          MongoContentRepository mongoContentRepository,
                                          @Value("${elasticsearch.index.name}") String indexName) {
        this.elasticsearchClient = elasticsearchClient;
        this.mongoContentRepository = mongoContentRepository;
        this.indexName = indexName;
    }
    
    public boolean validateTransfer() {
        try {
            long mongoCount = mongoContentRepository.count();
            logger.info("MongoDB document count: {}", mongoCount);
            
            CountRequest countRequest = CountRequest.of(c -> c.index(indexName));
            long elasticsearchCount = elasticsearchClient.count(countRequest).count();
            logger.info("Elasticsearch document count: {}", elasticsearchCount);
            
            boolean isValid = mongoCount == elasticsearchCount;
            
            if (isValid) {
                logger.info("Validation successful: All {} documents transferred successfully", mongoCount);
            } else {
                logger.error("Validation failed: MongoDB count ({}) does not match Elasticsearch count ({})", 
                    mongoCount, elasticsearchCount);
            }
            
            return isValid;
        } catch (Exception e) {
            logger.error("Error during validation", e);
            return false;
        }
    }
}

