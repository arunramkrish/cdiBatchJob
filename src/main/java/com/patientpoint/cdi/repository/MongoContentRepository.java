package com.patientpoint.cdi.repository;

import com.patientpoint.cdi.model.EditorialContent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MongoContentRepository extends MongoRepository<EditorialContent, UUID> {
    // All methods (findAll, count, etc.) are inherited from MongoRepository
    // which extends PagingAndSortingRepository and CrudRepository
}

