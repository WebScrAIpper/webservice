package com.polytech.webscraipper.repositories;

import com.polytech.webscraipper.dto.DocumentDto;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DocumentRepository extends MongoRepository<DocumentDto,String>{

}