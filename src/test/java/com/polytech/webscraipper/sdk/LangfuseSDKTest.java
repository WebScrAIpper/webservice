package com.polytech.webscraipper.sdk;

import static org.junit.jupiter.api.Assertions.*;

import com.polytech.webscraipper.WebScrAIpperApplication;
import com.polytech.webscraipper.sdk.responses.PromptResponse;
import com.polytech.webscraipper.sdk.responses.PromptsResponse;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = WebScrAIpperApplication.class)
public class LangfuseSDKTest {

  @Autowired private LangfuseSDK langfuseSDK;

  private final String VALID_PROMPT_NAME = "default-prompt";

  @Test
  public void shouldFetchAllPromptsSuccessfully() {
    List<PromptsResponse.IndividualPrompt> res = langfuseSDK.prompts.getAllPrompts();
    assertNotNull(res);
    System.out.println(res);
    assertFalse(res.isEmpty(), "Expected non-empty prompt list");
  }

  @Test
  public void shouldFetchSpecificPromptByName() {
    PromptResponse prompt = langfuseSDK.prompts.getByName(VALID_PROMPT_NAME, null, null);
    assertNotNull(prompt);
    assertEquals(VALID_PROMPT_NAME, prompt.name, "Prompt ID should match the requested ID");
  }

  @Test
  public void variablesGetNicelyExtractedFromText() {
    String text = "Hello {{name}}, how are you doing today? I hope you are {{mood}}.";
    List<String> variables = langfuseSDK.prompts.extractVariablesFromText(text);
    assertEquals(2, variables.size(), "Expected 2 variables");
    assertTrue(variables.contains("name"), "Expected 'name' variable");
    assertTrue(variables.contains("mood"), "Expected 'mood' variable");
  }

  @Test
  public void variablesGetNicelyReplacedInText() {
    String text = "Hello {{name}}, how are you doing today? I hope you are {{mood}}.";
    Map<String, String> variables = Map.of("name", "John", "mood", "happy");
    String newText = langfuseSDK.prompts.replacesVariablesInText(text, variables);
    assertEquals(
        "Hello John, how are you doing today? I hope you are happy.",
        newText,
        "Expected text with replaced variables");
  }

  @Test
  public void postATrace() {
    langfuseSDK.traces.postTrace(
        Map.of(
            "name", "LLM Request",
            "url", "none",
            "prompt", "default-prompt",
            "response", "Hello, how are you doing today?",
            "sessionId", "Test Session"));
  }
}
