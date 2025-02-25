package com.polytech.webscraipper.dto;

import org.springframework.data.annotation.Id;

public class ClassifierDto {
  @Id private String id;

  private String name;

  private String description;

  public ClassifierDto() {}

  public ClassifierDto(String name) {
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    ClassifierDto classifierDto = (ClassifierDto) obj;
    return name.equalsIgnoreCase(classifierDto.name);
  }
}
