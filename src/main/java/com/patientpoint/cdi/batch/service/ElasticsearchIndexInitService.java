package com.patientpoint.cdi.batch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ElasticsearchIndexInitService {
    
    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchIndexInitService.class);
    
    private final ElasticsearchClient elasticsearchClient;
    private final String indexName;
    
    public ElasticsearchIndexInitService(ElasticsearchClient elasticsearchClient,
                                        @Value("${elasticsearch.index.name}") String indexName) {
        this.elasticsearchClient = elasticsearchClient;
        this.indexName = indexName;
    }
    
    public void initializeIndex() {
        try {
            ExistsRequest existsRequest = ExistsRequest.of(e -> e.index(indexName));
            boolean exists = elasticsearchClient.indices().exists(existsRequest).value();
            
            if (!exists) {
                logger.info("Creating Elasticsearch index: {}", indexName);
                
                CreateIndexRequest createRequest = CreateIndexRequest.of(c -> c
                    .index(indexName)
                    .mappings(m -> m
                        .properties("id", p -> p.keyword(k -> k))
                        .properties("title", p -> p.text(t -> t
                            .fields("keyword", f -> f.keyword(k -> k))
                        ))
                        .properties("content", p -> p.text(t -> t))
                        .properties("metadata", p -> p.object(o -> o.dynamic()))
                        .properties("created_at", p -> p.date(d -> d.format("yyyy-MM-dd'T'HH:mm:ss")))
                        .properties("updated_at", p -> p.date(d -> d.format("yyyy-MM-dd'T'HH:mm:ss")))
                        .properties("indexed_at", p -> p.date(d -> d.format("yyyy-MM-dd'T'HH:mm:ss")))
                        .properties("status", p -> p.keyword(k -> k))
                    )
                );
                
                elasticsearchClient.indices().create(createRequest);
                logger.info("Elasticsearch index '{}' created successfully", indexName);
            } else {
                logger.info("Elasticsearch index '{}' already exists", indexName);
            }
        } catch (Exception e) {
            logger.error("Error initializing Elasticsearch index", e);
            throw new RuntimeException("Failed to initialize Elasticsearch index", e);
        }
    }
}

