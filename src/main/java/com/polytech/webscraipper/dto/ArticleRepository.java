package com.polytech.webscraipper.dto;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ArticleRepository extends MongoRepository<ArticleDto,String>{

}