package com.polytech.webscraipper.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.polytech.webscraipper.dto.CONTENT_TYPE;
import com.polytech.webscraipper.dto.DocumentDto;
import com.polytech.webscraipper.dto.SUPPORTED_LANGUAGES;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.List;

@org.springframework.data.mongodb.core.mapping.Document
public class Document {
  private String url;

  @JsonProperty("en_title")
  protected String enTitle;

  @JsonProperty("fr_title")
  protected String frTitle;

  protected String author;
  protected String date;
  protected List<String> imageUrls;
  protected int imageIndex;

  @JsonProperty("en_description")
  public String enDescription;

  @JsonProperty("fr_description")
  public String frDescription;

  @JsonProperty("content_type")
  public CONTENT_TYPE contentType;

  @JsonProperty("language_of_the_document")
  public SUPPORTED_LANGUAGES languageOfTheDocument;

  @DBRef
  public List<Classifier> classifiers;

  public Document() {}

  public Document(DocumentDto documentDto, List<Classifier> classifiers) {
    this.url = documentDto.getUrl();
    this.enTitle = documentDto.getEnTitle();
    this.frTitle = documentDto.getFrTitle();
    this.author = documentDto.getAuthor();
    this.date = documentDto.getDate();
    this.imageUrls = documentDto.getImageUrls();
    this.imageIndex = documentDto.getImageIndex();
    this.enDescription = documentDto.getEnDescription();
    this.frDescription = documentDto.getFrDescription();
    this.contentType = documentDto.getContent_type();
    this.languageOfTheDocument = documentDto.getLanguageOfTheDocument();
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
