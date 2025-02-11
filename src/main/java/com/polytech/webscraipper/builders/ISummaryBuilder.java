package com.polytech.webscraipper.builders;

import com.polytech.webscraipper.dto.DocumentDto;
import com.polytech.webscraipper.sdk.responses.PromptResponse;

public interface ISummaryBuilder {
  // Scrapping the website content
  public String scrapContent(String pageContent);

  // Generating the prompt dynamically
  public PromptResponse generatePrompt(String scrappedContent);

  // Requesting the AI
  public String requestAI(PromptResponse promptResponse);

  // Polishing the Answer
  public DocumentDto polishAnswer(String response);
}
