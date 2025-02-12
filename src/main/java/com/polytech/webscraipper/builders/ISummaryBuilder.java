package com.polytech.webscraipper.builders;

import com.polytech.webscraipper.dto.DocumentDto;
import com.polytech.webscraipper.exceptions.PromptException;
import com.polytech.webscraipper.exceptions.ScrappingException;
import com.polytech.webscraipper.sdk.responses.PromptResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public interface ISummaryBuilder {

  // Scrapping the website content
  String scrapContent(String url, String pageContent) throws ScrappingException;

  // Generating the prompt dynamically
  PromptResponse generatePrompt(String scrappedContent, List<String> classifiers);

  // Polishing the Answer
  DocumentDto polishAnswer(String url, DocumentDto response) throws PromptException;

  boolean isAnAppropriateBuilder(String url);
}
