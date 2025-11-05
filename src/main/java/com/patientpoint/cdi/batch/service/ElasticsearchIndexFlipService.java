package com.patientpoint.cdi.batch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.GetAliasRequest;
import co.elastic.clients.elasticsearch.indices.GetAliasResponse;
import co.elastic.clients.elasticsearch.indices.UpdateAliasesRequest;
import co.elastic.clients.elasticsearch.indices.update_aliases.Action;
import co.elastic.clients.elasticsearch.indices.update_aliases.AddAction;
import co.elastic.clients.elasticsearch.indices.update_aliases.RemoveAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ElasticsearchIndexFlipService {
    
    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchIndexFlipService.class);
    
    private final ElasticsearchClient elasticsearchClient;
    private final String indexName;
    private final String aliasName;
    
    public ElasticsearchIndexFlipService(ElasticsearchClient elasticsearchClient,
                                        @Value("${elasticsearch.index.name}") String indexName,
                                        @Value("${elasticsearch.index.alias}") String aliasName) {
        this.elasticsearchClient = elasticsearchClient;
        this.indexName = indexName;
        this.aliasName = aliasName;
    }
    
    public void flipIndex() {
        try {
            logger.info("Starting index flip for alias: {}", aliasName);
            
            // Get current aliases
            GetAliasRequest aliasRequest = GetAliasRequest.of(a -> a.name(aliasName));
            GetAliasResponse aliasResponse = elasticsearchClient.indices().getAlias(aliasRequest);
            
            List<Action> actions = new ArrayList<>();
            
            // Remove alias from all existing indices
            if (aliasResponse.result().size() > 0) {
                for (Map.Entry<String, ?> entry : aliasResponse.result().entrySet()) {
                    String existingIndex = entry.getKey();
                    if (!existingIndex.equals(indexName)) {
                        RemoveAction removeAction = RemoveAction.of(r -> r
                            .index(existingIndex)
                            .alias(aliasName)
                        );
                        actions.add(Action.of(a -> a.remove(removeAction)));
                        logger.info("Removing alias from index: {}", existingIndex);
                    }
                }
            }
            
            // Add alias to new index
            AddAction addAction = AddAction.of(a -> a
                .index(indexName)
                .alias(aliasName)
            );
            actions.add(Action.of(a -> a.add(addAction)));
            logger.info("Adding alias to index: {}", indexName);
            
            // Execute alias update
            UpdateAliasesRequest updateRequest = UpdateAliasesRequest.of(u -> u.actions(actions));
            elasticsearchClient.indices().updateAliases(updateRequest);
            
            logger.info("Index flip completed successfully. Alias '{}' now points to index '{}'", 
                aliasName, indexName);
        } catch (Exception e) {
            logger.error("Error during index flip", e);
            throw new RuntimeException("Failed to flip index", e);
        }
    }
}

