package com.polytech.webscraipper.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.polytech.webscraipper.dto.CONTENT_TYPE;
import com.polytech.webscraipper.dto.DocumentDto;
import com.polytech.webscraipper.dto.SUPPORTED_LANGUAGES;
import java.util.List;
import org.springframework.data.mongodb.core.mapping.DBRef;

@org.springframework.data.mongodb.core.mapping.Document
public class Document {

  @JsonProperty("url")
  private String url;

  @JsonProperty("en_title")
  protected String enTitle;

  @JsonProperty("fr_title")
  protected String frTitle;

  @JsonProperty("author")
  protected String author;

  @JsonProperty("date")
  protected String date;

  @JsonProperty("image_urls")
  protected List<String> imageUrls;

  @JsonProperty("image_index")
  protected int imageIndex;

  @JsonProperty("en_description")
  protected String enDescription;

  @JsonProperty("fr_description")
  protected String frDescription;

  @JsonProperty("content_type")
  protected CONTENT_TYPE contentType;

  @JsonProperty("language_of_the_document")
  protected SUPPORTED_LANGUAGES languageOfTheDocument;

  @JsonProperty("classifiers")
  @DBRef
  protected List<Classifier> classifiers;

  public Document() {}

  public Document(DocumentDto documentDto, List<Classifier> classifiers) {
    this.url = documentDto.url;
    this.enTitle = documentDto.enTitle;
    this.frTitle = documentDto.frTitle;
    this.author = documentDto.author;
    this.date = documentDto.date;
    this.imageUrls = documentDto.imageUrls;
    this.imageIndex = documentDto.imageIndex;
    this.enDescription = documentDto.enDescription;
    this.frDescription = documentDto.frDescription;
    this.contentType = documentDto.contentType;
    this.languageOfTheDocument = documentDto.languageOfTheDocument;
    this.classifiers = classifiers;
  }

  public Document(
      String url,
      String enTitle,
      String frTitle,
      String author,
      String date,
      List<String> imageUrls,
      int imageIndex,
      String en_description,
      String frDescription,
      CONTENT_TYPE contentType,
      SUPPORTED_LANGUAGES languageOfTheDocument,
      List<Classifier> classifiers) {
    this.url = url;
    this.enTitle = enTitle;
    this.frTitle = frTitle;
    this.author = author;
    this.date = date;
    this.imageUrls = imageUrls;
    this.imageIndex = imageIndex;
    this.enDescription = en_description;
    this.frDescription = frDescription;
    this.contentType = contentType;
    this.languageOfTheDocument = languageOfTheDocument;
    this.classifiers = classifiers;
  }
}
