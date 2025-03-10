package com.polytech.webscraipper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.polytech.webscraipper.builders.DefaultBuilder;
import com.polytech.webscraipper.builders.ISummaryBuilder;
import com.polytech.webscraipper.dto.DocumentDto;
import com.polytech.webscraipper.exceptions.DocumentException;
import com.polytech.webscraipper.exceptions.ScrappingException;
import com.polytech.webscraipper.sdk.LangfuseSDK;
import com.polytech.webscraipper.sdk.responses.PromptResponse;
import com.polytech.webscraipper.services.ClassifierService;
import com.polytech.webscraipper.utils.FunctionTimer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = WebScrAIpperApplication.class)
public class PipelineTests {
  private final BaseLogger logger = new BaseLogger(DefaultBuilder.class);
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired private ClassifierService classifierService;
  @Autowired private ChatModel chatModel;
  @Autowired private LangfuseSDK langfuseSDK;

  @Autowired private List<ISummaryBuilder> builders;
  @Autowired private DefaultBuilder defaultBuilder;

  private static final String SESSION_ID = new Date().toString();
  private static final String USER_ID = "PipelineTests";

  // @Test
  void testPipeline() throws IOException, DocumentException, ScrappingException {
    Map<String, String> urlToFileMap = new HashMap<>();
    urlToFileMap.put("https://angular.dev/", "src/test/resources/static/Angular.html");
    urlToFileMap.put("https://www.gehealthcare.fr/", "src/test/resources/static/gehealthcare.html");
    urlToFileMap.put(
        "https://cscalfani.medium.com/goodbye-object-oriented-programming-a59cda4c0e53",
        "src/test/resources/static/goodbyeOOP.html");
    urlToFileMap.put(
        "https://www.insimo.com/display-ultrasound/",
        "src/test/resources/static/insimoDisplay.html");
    urlToFileMap.put("https://www.keycloak.org/", "src/test/resources/static/keycloak.html");
    urlToFileMap.put("https://langfuse.com/", "src/test/resources/static/langfuse.html");
    urlToFileMap.put(
        "https://programmingisterrible.com/post/139222674273/write-code-that-is-easy-to-delete-not-easy-to",
        "src/test/resources/static/programmingisterrible.html");
    urlToFileMap.put("https://fr.react.dev/", "src/test/resources/static/react.html");
    urlToFileMap.put(
        "https://spring.io/projects/spring-boot", "src/test/resources/static/spring-boot.html");
    urlToFileMap.put("https://vuejs.org/", "src/test/resources/static/vuejs.html");

    List<DocumentDto> documentDtos = new ArrayList<>();

    for (Map.Entry<String, String> entry : urlToFileMap.entrySet()) {
      String url = entry.getKey();
      String filePath = entry.getValue();

      String siteName = getSiteName(url);
      logger.info("Processing: " + siteName);

      List<String> lines = Files.readAllLines(Paths.get(filePath));
      String content = String.join("\n", lines);

      // 1. Choose the Builder
      ISummaryBuilder builder =
          builders.stream()
              .filter(b -> b.isAnAppropriateBuilder(url))
              .findFirst()
              .orElse(defaultBuilder);

      // logger.info("Using builder: " + builder.getClass().getSimpleName());
      // 2. Scraping website content
      String scrappedContent = builder.scrapContent(url, content);

      // 3. Generating the prompt dynamically
      PromptResponse prompt =
          builder.generatePrompt(scrappedContent, classifierService.getAllClassifiersNames());

      DocumentDto documentDto;
      String aiAnswer = "No answer";
      try {
        // 4. Requesting the AI with timing and timeout handling
        var aiFullAnswer =
            FunctionTimer.timeExecutionWithTimeout(
                "AI Summary: " + url,
                () ->
                    chatModel.call(
                        new Prompt(
                            prompt.prompt,
                            OpenAiChatOptions.builder().model("gpt-4o-mini").build())),
                30,
                TimeUnit.SECONDS);

        aiAnswer = aiFullAnswer.getResult().getOutput().getText();
        // 5. Build a solid object from the AI response
        documentDto = objectMapper.readValue(aiAnswer, DocumentDto.class);

        // Building the response
        var res = builder.polishAnswer(url, documentDto);

        langfuseSDK.traces.postTrace(
            prompt.prompt,
            aiAnswer,
            SESSION_ID,
            Map.of("url", url),
            List.of("SUCCESS"),
            USER_ID,
            siteName);

        documentDtos.add(res);
      } catch (TimeoutException | InterruptedException | ExecutionException e) {
        throw new DocumentException("The AI request timed out or failed: " + e.getMessage());
      } catch (JsonProcessingException e) {
        langfuseSDK.traces.postTrace(
            prompt.prompt,
            aiAnswer,
            SESSION_ID,
            Map.of("url", url),
            List.of("ERROR"),
            e.getMessage(),
            siteName);

        logger.error("The AI response could not be parsed: " + aiAnswer);
        throw new DocumentException("The AI response could not be parsed: " + e.getMessage());
      }
    }

    // Wait for 10 seconds to let the API refresh
    try {
      Thread.sleep(10000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Thread was interrupted", e);
    }

    // Get the trace after waiting
    var traces = langfuseSDK.traces.getAllTraces(null, null, USER_ID, SESSION_ID);
    assertNotNull(traces, "Expected non-null trace list");
    assertEquals(10, traces.size(), "Expected 10 traces for the session");

    for (DocumentDto documentDto : documentDtos) {
      logger.info("DocumentDto: " + documentDto.author);
    }
  }

  private String getSiteName(String url) {
    if (url == null || url.isEmpty()) {
      return "Unknown";
    }
    String[] parts = url.split("//");
    if (parts.length > 1) {
      String domain = parts[1].split("/")[0];
      return domain.replace("www.", "");
    }
    return url;
  }
}
