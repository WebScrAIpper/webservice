package com.polytech.webscraipper.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.polytech.webscraipper.builders.DefaultBuilder;
import com.polytech.webscraipper.builders.ISummaryBuilder;
import com.polytech.webscraipper.dto.DocumentDto;
import com.polytech.webscraipper.exceptions.DocumentException;
import com.polytech.webscraipper.exceptions.ScrappingException;
import com.polytech.webscraipper.repositories.DocumentRepository;
import com.polytech.webscraipper.services.langfusesubservices.TracesManagementService;
import com.polytech.webscraipper.utils.FunctionTimer;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
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
  @Autowired private TracesManagementService tracesManagementService;

  @Autowired private List<ISummaryBuilder> builders;
  @Autowired private DefaultBuilder defaultBuilder;

  // current date as an id
  private static final String SESSION_ID = new Date().toString();

  public DocumentService() {}

  public Optional<DocumentDto> getDocumentByUrl(String url) {
    return documentRepo.findByUrl(url);
  }

  public List<DocumentDto> getAllDocuments() {
    return documentRepo.findAll();
  }

  /**
   * Build the summary of a website by selecting a {@link ISummaryBuilder} based on the url. If no
   * builder is found, the {@link DefaultBuilder} is used. The code is then divided into 4 steps: 1.
   * Scraping the website content. 2. Generating the prompt dynamically. 3. Requesting the AI with
   * timing and timeout handling. 4. Building an object strictly represented from the AI response
   * and polishing it. 5. Updating the environment with the new classifiers and saving the document.
   * <br>
   * It's recommended to implement a {@link ISummaryBuilder} for any content which is not text based
   * since the default builder is not built to handle video and audio. Any new builder will
   * automatically be added
   *
   * @param url the url of the website
   * @param content the content of the website
   * @return the document summary
   * @throws DocumentException if the document could not be built
   * @throws ScrappingException if the website could not be scrapped
   */
  public DocumentDto buildWebsiteSummary(String url, String content)
      throws DocumentException, ScrappingException {

    // 1. Choose the Builder
    ISummaryBuilder builder =
        builders.stream()
            .filter(b -> b.isAnAppropriateBuilder(url))
            .findFirst()
            .orElse(defaultBuilder);

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
      throw new DocumentException("The AI request timed out or failed: " + e.getMessage());
    } catch (JsonProcessingException e) {
      tracesManagementService.postFailedOutputAILog(prompt, e.getMessage(), url, SESSION_ID);
      throw new DocumentException("The AI response could not be parsed: " + e.getMessage());
    }
  }

  /**
   * Update the database when a summary is built. This function might be replaced by an api call to
   * the CGI api once it's ready.
   *
   * @param res the document to save
   */
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
}
