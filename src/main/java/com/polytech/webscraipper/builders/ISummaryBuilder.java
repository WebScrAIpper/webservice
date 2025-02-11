package com.polytech.webscraipper.builders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polytech.webscraipper.PromptException;
import com.polytech.webscraipper.dto.DocumentDto;
import com.polytech.webscraipper.sdk.responses.PromptResponse;
import com.polytech.webscraipper.services.ClassifierService;
import com.polytech.webscraipper.services.langfusesubservices.PromptManagementService;
import com.polytech.webscraipper.services.langfusesubservices.TracesManagementService;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public abstract class ISummaryBuilder {

  @Autowired protected ChatModel chatModel;
  @Autowired protected PromptManagementService promptManagementService;
  @Autowired protected TracesManagementService tracesManagementService;

  protected ObjectMapper objectMapper = new ObjectMapper();


  // Scrapping the website content
  public abstract String scrapContent(String pageContent);

  // Generating the prompt dynamically
  public abstract PromptResponse generatePrompt(String scrappedContent, List<String> classifiers);

  // Requesting the AI
  public abstract String requestAI(PromptResponse promptResponse);

  // Polishing the Answer
  public abstract DocumentDto polishAnswer(String url, String response) throws PromptException;

  public abstract boolean isAnAppropriateBuilder(String url);
}
