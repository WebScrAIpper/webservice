package com.polytech.webscraipper.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.data.mongodb.core.mapping.Document;

/**
    *             - title: the title of the article
    *             - author: the author of the article
    *             - date: the date of the article
    *             - image: the image that best represents the article (not required for now)
    *             - description: a short description of the article
    *             - content_type: the type of content (can take the values ARTICLE | VIDEO | PODCAST)
    *             - language: the language of the article
    *             - classifiers: a list of topics that the article covers
 */
@Document
public class AIFilledArticle {
    protected String title;
    protected String author;
    protected String date;
    protected String image;
    protected String description;
    protected CONTENT_TYPE content_type;
    @JsonDeserialize(using = SupportedLanguagesDeserializer.class)
    protected SUPPORTED_LANGUAGES language;
    protected String[] classifiers;

    public  AIFilledArticle() {
    }

    public AIFilledArticle(String title, String author, String date, String image, String description, CONTENT_TYPE content_type, SUPPORTED_LANGUAGES language, String[] classifiers) {
        this.title = title;
        this.author = author;
        this.date = date;
        this.image = image;
        this.description = description;
        this.content_type = content_type;
        this.language = language;
        this.classifiers = classifiers;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CONTENT_TYPE getContent_type() {
        return content_type;
    }

    public void setContent_type(CONTENT_TYPE content_type) {
        this.content_type = content_type;
    }

    public SUPPORTED_LANGUAGES getLanguage() {
        return language;
    }

    public void setLanguage(SUPPORTED_LANGUAGES language) {
        this.language = language;
    }

    public String[] getClassifiers() {
        return classifiers;
    }

    public void setClassifiers(String[] classifiers) {
        this.classifiers = classifiers;
    }
}

