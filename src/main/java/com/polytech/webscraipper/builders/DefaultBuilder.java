package com.polytech.webscraipper.builders;

import com.fasterxml.jackson.databind.JsonSerializable.Base;
import com.polytech.webscraipper.BaseLogger;
import com.polytech.webscraipper.dto.DocumentDto;
import com.polytech.webscraipper.exceptions.PromptException;
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
    var size = document.text().length();
    document.select("script, style, form, nav, aside, button, svg").remove();
    var newSize = document.text().length();
    logger.debug("Removed " + (size - newSize) + " characters from the document");
    // TODO: think about the iframe
    return document.toString();
  }

  @Override
  public PromptResponse generatePrompt(String scrappedContent, List<String> classifiers) {
    return promptManagementService.createDefaultProdPrompt(classifiers, scrappedContent);
  }

  @Override
  public DocumentDto polishAnswer(String url, DocumentDto documentDto) throws PromptException {

    documentDto.setUrl(url);
    return documentDto;
  }

  @Override
  public boolean isAnAppropriateBuilder(String url) {
    return !url.startsWith("https://www.youtube.com/watch?");
  }
}
