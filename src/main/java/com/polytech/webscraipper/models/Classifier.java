package com.polytech.webscraipper.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Classifier {
  @Id public String id;

  public String name;

  public String description;

  public Classifier() {}

  public Classifier(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Classifier classifier = (Classifier) obj;
    return name.equalsIgnoreCase(classifier.name);
  }
}
