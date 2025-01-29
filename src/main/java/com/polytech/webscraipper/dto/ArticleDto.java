package com.polytech.webscraipper.dto;

public class ArticleDto extends AIFilledArticle {
    private String url;

    public ArticleDto() {
    }

    public ArticleDto(String url, String title, String author, String date, String image, String description, String content_type, String language, String[] classifiers) {
        super(title, author, date, image, description, content_type, language, classifiers);
        this.url = url;
    }

    public ArticleDto(AIFilledArticle dto, String url) {
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
