package com.polytech.webscraipper.repositories;

import com.polytech.webscraipper.dto.ArticleDto;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ArticleRepository extends MongoRepository<ArticleDto,String>{

}