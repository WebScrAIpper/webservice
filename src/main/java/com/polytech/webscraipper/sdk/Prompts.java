package com.polytech.webscraipper.sdk;

import com.polytech.webscraipper.sdk.responses.PromptResponse;
import com.polytech.webscraipper.sdk.responses.PromptsResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

public class Prompts {

  private final PromptsClient promptsClient;

  public Prompts(PromptsClient promptsClient) {
    this.promptsClient = promptsClient;
  }

  public List<String> getPromptVariables(String promptName, Integer version, String label) {
    PromptResponse prompt = getByName(promptName, version, label);
    return extractVariablesFromText(prompt.prompt);
  }

  public PromptResponse getCustomizedPrompt(
      String promptName, Integer version, String label, Map<String, String> variables) {
    PromptResponse prompt = getByName(promptName, version, label);
    prompt.prompt = replacesVariablesInText(prompt.prompt, variables);
    return prompt;
  }

  public List<PromptsResponse.IndividualPrompt> getAllPrompts(
      String name,
      String label,
      String tag,
      Integer page,
      Integer limit,
      String fromUpdatedAt,
      String toUpdatedAt) {
    return promptsClient.getAllPrompts(name, label, tag, page, limit, fromUpdatedAt, toUpdatedAt)
        .data;
  }

  public List<PromptsResponse.IndividualPrompt> getAllPrompts(
      String name, String label, String tag, Integer page, Integer limit, String fromUpdatedAt) {
    return promptsClient.getAllPrompts(name, label, tag, page, limit, fromUpdatedAt, null).data;
  }

  public List<PromptsResponse.IndividualPrompt> getAllPrompts(
      String name, String label, String tag, Integer page, Integer limit) {
    return promptsClient.getAllPrompts(name, label, tag, page, limit, null, null).data;
  }

  public List<PromptsResponse.IndividualPrompt> getAllPrompts(
      String name, String label, String tag, Integer page) {
    return promptsClient.getAllPrompts(name, label, tag, page, null, null, null).data;
  }

  public List<PromptsResponse.IndividualPrompt> getAllPrompts(
      String name, String label, String tag) {
    return promptsClient.getAllPrompts(name, label, tag, null, null, null, null).data;
  }

  public List<PromptsResponse.IndividualPrompt> getAllPrompts(String name, String label) {
    return promptsClient.getAllPrompts(name, label, null, null, null, null, null).data;
  }

  public List<PromptsResponse.IndividualPrompt> getAllPrompts(String name) {
    return promptsClient.getAllPrompts(name, null, null, null, null, null, null).data;
  }

  public List<PromptsResponse.IndividualPrompt> getAllPrompts() {
    return promptsClient.getAllPrompts(null, null, null, null, null, null, null).data;
  }

  public PromptResponse getByName(String promptName, Integer version, String label) {
    return promptsClient.getPromptByName(promptName, version, label);
  }

  @FeignClient(
      name = "langfuse-sdk-prompts",
      url = "https://cloud.langfuse.com/api/public/v2/prompts",
      configuration = FeignConfig.class)
  public interface PromptsClient {

    @GetMapping("/")
    PromptsResponse getAllPrompts(
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "label", required = false) String label,
        @RequestParam(value = "tag", required = false) String tag,
        @RequestParam(value = "page", required = false) Integer page,
        @RequestParam(value = "limit", required = false) Integer limit,
        @RequestParam(value = "fromUpdatedAt", required = false) String fromUpdatedAt,
        @RequestParam(value = "toUpdatedAt", required = false) String toUpdatedAt);

    @GetMapping("/{promptId}")
    PromptResponse getPromptByName(
        @PathVariable("promptId") String promptId,
        @RequestParam(value = "version", required = false) Integer version,
        @RequestParam(value = "label", required = false) String label);
  }

  public List<String> extractVariablesFromText(String text) {
    Pattern pattern = Pattern.compile("\\{\\{([^}]*)}}");
    Matcher matcher = pattern.matcher(text);
    List<String> variables = new ArrayList<>();
    while (matcher.find()) {
      variables.add(matcher.group(1));
    }
    return variables;
  }

  public String replacesVariablesInText(String text, Map<String, String> variables) {
    List<String> requiredVariables = extractVariablesFromText(text);
    if (!variables.keySet().containsAll(requiredVariables)) {
      throw new IllegalArgumentException("Not all variables have a value");
    }
    for (Map.Entry<String, String> entry : variables.entrySet()) {
      text = text.replace("{{" + entry.getKey() + "}}", entry.getValue());
    }
    return text;
  }
}
