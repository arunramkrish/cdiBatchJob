package com.patientpoint.cdi.batch.reader;

import com.patientpoint.cdi.model.EditorialContent;
import com.patientpoint.cdi.repository.MongoContentRepository;
import org.springframework.batch.item.data.AbstractPaginatedDataItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

@Component
public class MongoContentItemReader extends AbstractPaginatedDataItemReader<EditorialContent> {
    
    private final MongoContentRepository repository;
    private final int pageSize;
    
    public MongoContentItemReader(MongoContentRepository repository, 
                                  @Value("${batch.job.chunk-size:1000}") int pageSize) {
        this.repository = repository;
        this.pageSize = pageSize;
        setName("mongoContentItemReader");
    }
    
    @Override
    protected Iterator<EditorialContent> doPageRead() {
        Pageable pageable = PageRequest.of(getPage(), pageSize);
        Page<EditorialContent> page = repository.findAll(pageable);
        List<EditorialContent> content = page.getContent();
        return content.iterator();
    }
}

