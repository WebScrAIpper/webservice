package com.polytech.webscraipper.repositories;

import com.polytech.webscraipper.dto.DocumentDto;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface DocumentRepository extends MongoRepository<DocumentDto,String>{
    Optional<DocumentDto> findByUrl(String url);
}