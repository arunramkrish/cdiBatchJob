package com.patientpoint.cdi.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientOptions;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Configuration
public class ElasticsearchConfig {
    
    @Value("${elasticsearch.serverUrl}")
    private String serverUrl;
    
    @Value("${elasticsearch.apikey}")
    private String apikey;
    
    @Bean
    public ElasticsearchClient elasticsearchClient() {
        URI uri = URI.create(serverUrl);
        
        // Configure Jackson mapper with JSR310 module for LocalDateTime support
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        // Build Elasticsearch client with API key
        // Note: When Elasticsearch security is disabled, the API key header is sent but ignored
        return ElasticsearchClient.of(b -> b
            .host(uri)
            .apiKey(apikey)
            .options(RestClientOptions.of(o -> o
                .addHeader("Content-Type", "application/json")
            ))
            .jsonpMapper(new co.elastic.clients.json.jackson.JacksonJsonpMapper(objectMapper))
        );
    }
}

