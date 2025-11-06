package com.patientpoint.cdi.batch.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CdiJobLauncher implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(CdiJobLauncher.class);
    
    @Autowired
    private JobLauncher jobLauncher;
    
    @Autowired
    private Job elasticsearchSyncJob;
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting Elasticsearch sync job...");
        
        JobParameters jobParameters = new JobParametersBuilder()
            .addLong("timestamp", System.currentTimeMillis())
            .toJobParameters();
        
        try {
            jobLauncher.run(elasticsearchSyncJob, jobParameters);
            logger.info("Job execution completed");
        } catch (Exception e) {
            logger.error("Job execution failed", e);
            throw e;
        }
    }
}

