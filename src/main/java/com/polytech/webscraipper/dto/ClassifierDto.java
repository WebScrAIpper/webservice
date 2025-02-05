package com.polytech.webscraipper.dto;

import org.springframework.data.annotation.Id;

public class ClassifierDto {
  @Id private String id;

  private String name;

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

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return false;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    ClassifierDto classifierDto = (ClassifierDto) obj;
    return name.equals(classifierDto.name);
  }
}
