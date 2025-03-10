package com.polytech.webscraipper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

public class DocumentDto {
  public String url;

  @JsonProperty("en_title")
  public String enTitle;

  @JsonProperty("fr_title")
  public String frTitle;

  public String author;
  public String date;

  @JsonProperty("image_urls")
  public List<String> imageUrls;

  @JsonProperty("image_index")
  public int imageIndex;

  @JsonProperty("en_description")
  public String enDescription;

  @JsonProperty("fr_description")
  public String frDescription;

  @JsonProperty("content_type")
  public CONTENT_TYPE contentType;

  @JsonDeserialize(using = SupportedLanguagesDeserializer.class)
  @JsonProperty("language_of_the_document")
  public SUPPORTED_LANGUAGES languageOfTheDocument;

  public List<String> classifiers;

  public DocumentDto() {}

  public DocumentDto(
      String url,
      String enTitle,
      String frTitle,
      String author,
      String date,
      List<String> imageUrls,
      int imageIndex,
      String enDescription,
      String frDescription,
      CONTENT_TYPE contentType,
      SUPPORTED_LANGUAGES languageOfTheDocument,
      List<String> classifiers) {
    this.url = url;
    this.enTitle = enTitle;
    this.frTitle = frTitle;
    this.author = author;
    this.date = date;
    this.imageUrls = imageUrls;
    this.imageIndex = imageIndex;
    this.enDescription = enDescription;
    this.frDescription = frDescription;
    this.contentType = contentType;
    this.languageOfTheDocument = languageOfTheDocument;
    this.classifiers = classifiers;
  }
}
