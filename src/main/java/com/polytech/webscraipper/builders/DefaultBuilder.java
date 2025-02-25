package com.polytech.webscraipper.builders;

import com.polytech.webscraipper.BaseLogger;
import com.polytech.webscraipper.dto.DocumentDto;
import com.polytech.webscraipper.exceptions.DocumentException;
import com.polytech.webscraipper.sdk.LangfuseSDK;
import com.polytech.webscraipper.sdk.responses.PromptResponse;
import com.polytech.webscraipper.services.langfusesubservices.PromptManagementService;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultBuilder implements ISummaryBuilder {

  @Autowired PromptManagementService promptManagementService;

  private BaseLogger logger = new BaseLogger(DefaultBuilder.class);
  @Autowired private LangfuseSDK langfuseSDK;

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
    return langfuseSDK.prompts.getCustomizedPrompt(
        "default-prompt",
        null,
        "latest",
        Map.of("classifiers", classifiers.toString(), "content", scrappedContent));
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
