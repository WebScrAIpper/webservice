package com.polytech.webscraipper.services.langfusesubservices;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.polytech.webscraipper.BaseLogger;
import com.polytech.webscraipper.sdk.LangfuseSDK;
import com.polytech.webscraipper.sdk.responses.PromptResponse;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PromptManagementService extends BaseLogger {

  private static final String PRODUCTION = "production";

  private static final String DEFAULT_PROMPT_NAME = "default-prompt";
  private static final String YOUTUBE_PROMPT_NAME = "youtube-prompt";

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired private LangfuseSDK langfuseSDK;

  public List<String> getProdPromptsNames() {
    return langfuseSDK.prompts.getAllPrompts(null, "production").stream()
        .map(prompt -> prompt.name)
        .toList();
  }

  public PromptResponse createDefaultProdPrompt(List<String> classifiersNames, String content) {
    String classifiersJson;
    try {
      classifiersJson = objectMapper.writeValueAsString(classifiersNames);
    } catch (JsonProcessingException e) {
      logger.error(
          "Error while writing the classifiers into the prompt, giving an empty list to the model");
      classifiersJson = "[]";
    }
    Map<String, String> variables = Map.of("classifiers", classifiersJson, "content", content);

    return langfuseSDK.prompts.getCustomizedPrompt(
        DEFAULT_PROMPT_NAME, null, "production", variables);
  }

  // Note: The behaviour for both is almost the same for now but could change in future prompt
  // improvements.

  public PromptResponse createYouTubeProdPrompt(List<String> classifiersNames, String content) {
    String classifiersJson;
    try {
      classifiersJson = objectMapper.writeValueAsString(classifiersNames);
    } catch (JsonProcessingException e) {
      logger.error(
          "Error while writing the classifiers into the prompt, giving an empty list to the model");
      classifiersJson = "[]";
    }
    Map<String, String> variables = Map.of("classifiers", classifiersJson, "content", content);

    return langfuseSDK.prompts.getCustomizedPrompt(
        YOUTUBE_PROMPT_NAME, null, "production", variables);
  }
}
