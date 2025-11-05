package com.patientpoint.cdi.batch.writer;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.IndexOperation;
import com.patientpoint.cdi.model.ElasticsearchContent;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ElasticsearchItemWriter implements ItemWriter<ElasticsearchContent> {
    
    private final ElasticsearchClient elasticsearchClient;
    private final String indexName;
    
    public ElasticsearchItemWriter(ElasticsearchClient elasticsearchClient, 
                                   @Value("${elasticsearch.index.name}") String indexName) {
        this.elasticsearchClient = elasticsearchClient;
        this.indexName = indexName;
    }
    
    @Override
    public void write(Chunk<? extends ElasticsearchContent> chunk) throws Exception {
        List<BulkOperation> bulkOperations = new ArrayList<>();
        
        for (ElasticsearchContent content : chunk.getItems()) {
            IndexOperation<ElasticsearchContent> indexOp = IndexOperation.of(i -> i
                .index(indexName)
                .id(content.getId())
                .document(content)
            );
            bulkOperations.add(BulkOperation.of(op -> op.index(indexOp)));
        }
        
        BulkRequest bulkRequest = BulkRequest.of(r -> r.operations(bulkOperations));
        
        var response = elasticsearchClient.bulk(bulkRequest);
        
        if (response.errors()) {
            throw new RuntimeException("Elasticsearch bulk operation failed: " + response.items());
        }
    }
}

