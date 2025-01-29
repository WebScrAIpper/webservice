package com.polytech.webscraipper.repositories;

import com.polytech.webscraipper.dto.ArticleDto;
import com.polytech.webscraipper.dto.ClassifierDto;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ClassifierRepository extends MongoRepository<ClassifierDto,String>{

}