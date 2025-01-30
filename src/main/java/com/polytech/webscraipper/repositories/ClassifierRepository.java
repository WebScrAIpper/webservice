package com.polytech.webscraipper.repositories;

import com.polytech.webscraipper.dto.ClassifierDto;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClassifierRepository extends MongoRepository<ClassifierDto,String>{

}