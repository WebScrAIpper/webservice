package com.polytech.webscraipper.repositories;

import com.polytech.webscraipper.models.Document;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DocumentRepository extends MongoRepository<Document, String> {
  Optional<Document> findByUrl(String url);
}
