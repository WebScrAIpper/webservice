package com.polytech.webscraipper.dto;

public class DocumentDto extends AIFilledDocument {
    private String url;

    public DocumentDto() {
    }

    public DocumentDto(String url, String title, String author, String date, String image, String description, CONTENT_TYPE content_type, SUPPORTED_LANGUAGES language, String[] classifiers) {
        super(title, author, date, image, description, content_type, language, classifiers);
        this.url = url;
    }

    public DocumentDto(AIFilledDocument dto, String url) {
        super(dto.getTitle(), dto.getAuthor(), dto.getDate(), dto.getImage(), dto.getDescription(), dto.getContent_type(), dto.getLanguage(), dto.getClassifiers());
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }




}
