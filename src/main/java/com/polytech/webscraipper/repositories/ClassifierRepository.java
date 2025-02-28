package com.polytech.webscraipper.repositories;

import com.polytech.webscraipper.models.Classifier;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClassifierRepository extends MongoRepository<Classifier, String> {}
