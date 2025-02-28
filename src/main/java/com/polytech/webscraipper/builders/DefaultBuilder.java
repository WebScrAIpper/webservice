package com.polytech.webscraipper.builders;

import com.polytech.webscraipper.BaseLogger;
import com.polytech.webscraipper.dto.DocumentDto;
import com.polytech.webscraipper.exceptions.DocumentException;
import com.polytech.webscraipper.sdk.responses.PromptResponse;
import com.polytech.webscraipper.services.langfusesubservices.PromptManagementService;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultBuilder implements ISummaryBuilder {

  @Autowired PromptManagementService promptManagementService;

  private BaseLogger logger = new BaseLogger(DefaultBuilder.class);

  public DefaultBuilder() {}

  @Override
  public String scrapContent(String url, String pageContent) {
    Document document = Jsoup.parse(pageContent);
    int originalSize = document.text().length();

    // Remove unnecessary elements
    document
        .select("script, style, form, nav, aside, button, svg, footer, header, link[rel]")
        .remove();

    int basicScrapping = document.text().length();
    logger.debug(
        "Removed "
            + (originalSize - basicScrapping)
            + " characters from the document with basic scraping");
            
    // Remove all ads
    document.select("div[class*='ad'], div[id*='ad'],div[name*='ad']").remove();

    // Remove related articles (often in sections with "related" or "recommend")
    document
        .select(
            "div[class*='related'], section[class*='related'], div[id*='related'], div[class*='recommend']")
        .remove();

    // Remove sponsored content
    document.select("div[class*='sponsored'], div[id*='sponsored']").remove();

    // Remove all <a> tags and their content
    document.select("a").remove();

    // Remove <link> elements like <link rel="preconnect">
    document.select("link").remove();

    // Remove <div> elements that don't contain any text
    document
        .select("div")
        .forEach(
            element -> {
              if (element.text().isEmpty()) {
                element.remove();
              }
            });

    int extraScrapping = document.text().length();
    logger.debug(
        "Removed "
            + (basicScrapping - extraScrapping)
            + " characters from the document with the extra scraping");

    return document.toString();
  }

  @Override
  public PromptResponse generatePrompt(String scrappedContent, List<String> classifiers) {
    return promptManagementService.createDefaultProdPrompt(classifiers, scrappedContent);
  }

  @Override
  public DocumentDto polishAnswer(String url, DocumentDto documentDto) throws DocumentException {

    documentDto.setUrl(url);
    return documentDto;
  }

  @Override
  public boolean isAnAppropriateBuilder(String url) {
    // Since it's the default builder, it's selected by default and should during the selection
    // phase
    return false;
  }
}
