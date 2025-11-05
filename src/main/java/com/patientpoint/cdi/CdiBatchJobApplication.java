package com.patientpoint.cdi;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableBatchProcessing
@EnableMongoRepositories(basePackages = "com.patientpoint.cdi.repository")
@EnableJpaRepositories
public class CdiBatchJobApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(CdiBatchJobApplication.class, args);
    }
}

