package com.polytech.webscraipper.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.polytech.webscraipper.builders.ISummaryBuilder;
import com.polytech.webscraipper.dto.DocumentDto;
import com.polytech.webscraipper.exceptions.PromptException;
import com.polytech.webscraipper.exceptions.ScrappingException;
import com.polytech.webscraipper.repositories.DocumentRepository;
import com.polytech.webscraipper.services.langfusesubservices.PromptManagementService;
import com.polytech.webscraipper.services.langfusesubservices.TracesManagementService;
import com.polytech.webscraipper.utils.FunctionTimer;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired private DocumentRepository documentRepo;
  @Autowired private ClassifierService classifierService;
  @Autowired private ChatModel chatModel;
  @Autowired private PromptManagementService promptManagementService;
  @Autowired private TracesManagementService tracesManagementService;

  private final List<ISummaryBuilder> builders;

  // current date as an id
  private static final String SESSION_ID = new Date().toString();

  public DocumentService(List<ISummaryBuilder> builders) {
    this.builders = builders;
  }

  public Optional<DocumentDto> getDocumentByUrl(String url) {
    return documentRepo.findByUrl(url);
  }

  public List<DocumentDto> getAllDocuments() {
    return documentRepo.findAll();
  }

  public DocumentDto buildWebsiteSummary(String url, String content)
      throws PromptException, ScrappingException {

    // 1. Choose the Builder
    ISummaryBuilder builder =
        builders.stream()
            .filter(b -> b.isAnAppropriateBuilder(url))
            .findFirst()
            .orElseThrow(
                () -> new PromptException("No appropriate builder found for the given URL"));

    System.out.println("Using builder: " + builder.getClass().getSimpleName());

    // 2. Scraping website content
    String scrappedContent = builder.scrapContent(url, content);

    // 3. Generating the prompt dynamically
    var prompt = builder.generatePrompt(scrappedContent, classifierService.getAllClassifiers());

    DocumentDto documentDto;
    try {
      // 4. Requesting the AI with timing and timeout handling
      var aiFullAnswer =
          FunctionTimer.timeExecutionWithTimeout(
              "AI Summary: " + url,
              () ->
                  chatModel.call(
                      new Prompt(
                          prompt.prompt, OpenAiChatOptions.builder().model("gpt-4o-mini").build())),
              30,
              TimeUnit.SECONDS);

      var aiAnswer = aiFullAnswer.getResult().getOutput().getText();

      // 5. Build a solid object from the AI response
      documentDto = objectMapper.readValue(aiAnswer, DocumentDto.class);

      // Building the response
      var res = builder.polishAnswer(url, documentDto);

      System.out.println(objectMapper.writeValueAsString(res));

      updateDatabase(res);

      tracesManagementService.postGenericAILog(prompt, res.toString(), url, SESSION_ID);

      return res;
    } catch (TimeoutException | InterruptedException | ExecutionException e) {
      throw new PromptException("The AI request timed out or failed: " + e.getMessage());
    } catch (JsonProcessingException e) {
      tracesManagementService.postFailedOutputAILog(prompt, e.getMessage(), url, SESSION_ID);
      throw new PromptException("The AI response could not be parsed: " + e.getMessage());
    }
  }

  private void updateDatabase(DocumentDto res) {
    // Handle Classifiers
    var newClassifiers =
        Arrays.stream(res.getClassifiers())
            .filter(classifier -> !classifierService.getAllClassifiers().contains(classifier))
            .toArray(String[]::new);
    for (String newClassifier : newClassifiers) {
      classifierService.addClassifier(newClassifier);
    }

    // Saving the document
    documentRepo.save(res);
  }

  // public DocumentDto buildYoutubeVodSummary(String url)
  //     throws IOException, PromptException, InterruptedException {

  // Scraping vod content (transcript & metas)
  // String content = scrapYoutubeVod(url);

  // Generating the prompt dynamically
  // var prompt =
  //    promptManagementService.createYouTubeProdPrompt(
  //        classifierService.getAllClassifiers(), content);

  // Requesting the AI
  // var aiAnswer = requestToAi(prompt.prompt);

  // return aiAnswer;
  // }

  public String scrapContent(String content) {
    Document document = Jsoup.parse(content);

    document.select("script, style, form, nav, aside, button, svg").remove();
    // TODO: think about the iframe
    return document.toString();
  }

  public DocumentDto requestToAi(String prompt) throws PromptException {
    try {
      var aiAnswer =
          chatModel.call(
              new Prompt(prompt, OpenAiChatOptions.builder().model("gpt-4o-mini").build()));

      String aiResponseText = aiAnswer.getResult().getOutput().getText();
      if (aiResponseText == null || aiResponseText.isEmpty()) {
        throw new IOException("AI returned an empty or invalid response.");
      }

      System.out.println(aiResponseText);
      return objectMapper.readValue(aiResponseText, DocumentDto.class);

    } catch (IOException e) {
      // TODO: We might need cleaner error message for a better prompt tracking
      throw new PromptException(
          "The LLM did not succeed to fill the summary successfully\n" + e.getMessage());
    }
  }
}
