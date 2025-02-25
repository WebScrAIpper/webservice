package com.polytech.webscraipper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

public class DocumentDto {
  private String url;

  @JsonProperty("en_title")
  protected String en_title;

  @JsonProperty("fr_title")
  protected String fr_title;

  protected String author;
  protected String date;
  protected List<String> imageUrls;
  protected int imageIndex;

  @JsonProperty("en_description")
  protected String en_description;

  @JsonProperty("fr_description")
  protected String fr_description;

  protected CONTENT_TYPE content_type;

  @JsonDeserialize(using = SupportedLanguagesDeserializer.class)
  protected SUPPORTED_LANGUAGES languageOfTheDocument;

  protected List<String> classifiers;

  public DocumentDto() {}

  public DocumentDto(
      String url,
      String en_title,
      String fr_title,
      String author,
      String date,
      List<String> imageUrls,
      int imageIndex,
      String en_description,
      String fr_description,
      CONTENT_TYPE content_type,
      SUPPORTED_LANGUAGES languageOfTheDocument,
      List<String> classifiers) {
    this.url = url;
    this.en_title = en_title;
    this.fr_title = fr_title;
    this.author = author;
    this.date = date;
    this.imageUrls = imageUrls;
    this.imageIndex = imageIndex;
    this.en_description = en_description;
    this.fr_description = fr_description;
    this.content_type = content_type;
    this.languageOfTheDocument = languageOfTheDocument;
    this.classifiers = classifiers;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getEn_title() {
    return en_title;
  }

  public void setEn_title(String en_title) {
    this.en_title = en_title;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public List<String> getImageUrls() {
    return imageUrls;
  }

  public void setImageUrls(List<String> imageUrls) {
    this.imageUrls = imageUrls;
  }

  public int getImageIndex() {
    return imageIndex;
  }

  public void setImageIndex(int imageIndex) {
    this.imageIndex = imageIndex;
  }

  public String getEn_description() {
    return en_description;
  }

  public void setEn_description(String en_description) {
    this.en_description = en_description;
  }

  public String getFr_title() {
    return fr_title;
  }

  public void setFr_title(String fr_title) {
    this.fr_title = fr_title;
  }

  public CONTENT_TYPE getContent_type() {
    return content_type;
  }

  public void setContent_type(CONTENT_TYPE content_type) {
    this.content_type = content_type;
  }

  public SUPPORTED_LANGUAGES getLanguageOfTheDocument() {
    return languageOfTheDocument;
  }

  public void setLanguageOfTheDocument(SUPPORTED_LANGUAGES languageOfTheDocument) {
    this.languageOfTheDocument = languageOfTheDocument;
  }

  public List<String> getClassifiers() {
    return classifiers;
  }

  public void setClassifiers(List<String> classifiers) {
    this.classifiers = classifiers;
  }
}
