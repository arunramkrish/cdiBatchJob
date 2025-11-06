package com.patientpoint.cdi.batch.reader;

import com.patientpoint.cdi.model.EditorialContent;
import com.patientpoint.cdi.repository.EditorialContentRepository;
import org.springframework.batch.item.data.AbstractPaginatedDataItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

@Component
public class ContentDataReader extends AbstractPaginatedDataItemReader<EditorialContent> {
    
    private final EditorialContentRepository repository;
    private final int pageSize;
    
    public ContentDataReader(EditorialContentRepository repository, 
                                  @Value("${batch.job.chunk-size:1000}") int pageSize) {
        this.repository = repository;
        this.pageSize = pageSize;
        setName("contentDataReader");
    }
    
    @Override
    protected Iterator<EditorialContent> doPageRead() {
        Pageable pageable = PageRequest.of(getPage(), pageSize);
        Page<EditorialContent> page = repository.findAll(pageable);
        List<EditorialContent> content = page.getContent();
        return content.iterator();
    }
}

