package com.polytech.webscraipper.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.polytech.webscraipper.PromptException;
import com.polytech.webscraipper.dto.AIFilledDocument;
import com.polytech.webscraipper.dto.DocumentDto;
import com.polytech.webscraipper.repositories.DocumentRepository;
import com.polytech.webscraipper.services.langfusesubservices.PromptManagementService;
import com.polytech.webscraipper.services.langfusesubservices.TracesManagementService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
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

  // current date as an id
  private static final String SESSION_ID = new Date().toString();

  public DocumentService() {}

  public Optional<DocumentDto> getDocumentByUrl(String url) {
    return documentRepo.findByUrl(url);
  }

  public List<DocumentDto> getAllDocuments() {
    return documentRepo.findAll();
  }

  public DocumentDto buildWebsiteSummary(String url, String content)
      throws IOException, PromptException {

    // Scraping website content
    String scrappedContent = scrapContent(content);

    // Generating the prompt dynamically
    var prompt =
        promptManagementService.createDefaultProdPrompt(
            classifierService.getAllClassifiers(), scrappedContent);

    // Requesting the AI
    var timeAtStart = System.currentTimeMillis();
    // Added a time logger since sometime the OpenAI API is overloaded and takes a long time to
    // answer.
    var timerLogger = new Timer();
    try {
      System.out.println(
          "Requesting the AI for the prompt: " + prompt.getFirst().substring(0, 100) + "...");

      timerLogger.scheduleAtFixedRate(
          new TimerTask() {
            @Override
            public void run() {
              var timeAtNow = System.currentTimeMillis();
              System.out.println(
                  "Waiting for the OpenAI Api to answer since: "
                      + (timeAtNow - timeAtStart) / 1000
                      + "seconds.");
            }
          },
          5000,
          5000);
      var aiAnswer = requestToAi(prompt.getFirst());
      var res = buildAnswer(aiAnswer, url);

      tracesManagementService.postGenericAILog(
          prompt.getSecond(), aiAnswer.toString(), url, SESSION_ID);
      return res;
    } catch (Exception e) {
      tracesManagementService.postFailedOutputAILog(
          prompt.getSecond(), e.getMessage(), url, SESSION_ID);
      throw e;
    } finally {
      timerLogger.cancel();
      var timeAtEnd = System.currentTimeMillis();
      System.out.println(
              "The OpenAI API answered in " + (timeAtEnd - timeAtStart) / 1000 + "seconds.");
    }
  }

  public DocumentDto buildYoutubeVodSummary(String url) throws IOException, PromptException {

    // Scraping vod content (transcript & metas)
    String content = scrapYoutubeVod(url);

    // Generating the prompt dynamically
    var prompt =
        promptManagementService.createYouTubeProdPrompt(
            classifierService.getAllClassifiers(), content);

    // Requesting the AI
    var aiAnswer = requestToAi(prompt.getFirst());
    return buildAnswer(aiAnswer, url);
  }

  public String scrapContent(String content) {
    Document document = Jsoup.parse(content);

    document.select("script, style, form, nav, aside, button, svg").remove();
    // TODO: think about the iframe
    return document.toString();
  }

  private String scrapYoutubeVod(String url) {
    try {
      // Get vod metadata
      String videoInfoJson = executePythonScript("src/scripts/get_yt_infos.py", url);

      // Get transcript
      String transcript = executePythonScript("src/scripts/get_transcript.py", url);

      Map<String, String> result = new HashMap<>();
      result.put("metadata", videoInfoJson);
      result.put("transcript", transcript);

      Gson gson = new Gson();
      return gson.toJson(result);
    } catch (Exception e) {
      return "{\"error\": \"" + e.getMessage() + "\"}";
    }
  }

  public AIFilledDocument requestToAi(String prompt) throws PromptException {
    try {
      var aiAnswer =
          chatModel.call(
              new Prompt(prompt, OpenAiChatOptions.builder().model("gpt-4o-mini").build()));

      String aiResponseText = aiAnswer.getResult().getOutput().getText();
      if (aiResponseText == null || aiResponseText.isEmpty()) {
        throw new IOException("AI returned an empty or invalid response.");
      }

      System.out.println(aiResponseText);
      return objectMapper.readValue(aiResponseText, AIFilledDocument.class);

    } catch (IOException e) {
      // TODO: We might need cleaner error message for a better prompt tracking
      throw new PromptException(
          "The LLM did not succeed to fill the summary successfully\n" + e.getMessage());
    }
  }

  public DocumentDto buildAnswer(AIFilledDocument aiAnswer, String url) {
    // Building the response
    DocumentDto documentDto = new DocumentDto(aiAnswer, url);

    // Handle Classifiers
    var newClassifiers =
        Arrays.stream(aiAnswer.getClassifiers())
            .filter(classifier -> !classifierService.getAllClassifiers().contains(classifier))
            .toArray(String[]::new);
    for (String newClassifier : newClassifiers) {
      classifierService.addClassifier(newClassifier);
    }

    // Saving the document
    documentRepo.save(documentDto);
    return documentDto;
  }

  // TODO: think about moving this method somewhere else
  public String executePythonScript(String scriptPath, String url)
      throws IOException, InterruptedException {
    // TODO: Doesn't work for everyone, venv does not always have a bin folder.
    ProcessBuilder pb = new ProcessBuilder("src/.venv/bin/python3", scriptPath, url);
    Process process = pb.start();

    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    StringBuilder output = new StringBuilder();
    String line;

    while ((line = reader.readLine()) != null) {
      output.append(line).append("\n");
    }
    process.waitFor();

    return output.toString().trim();
  }
}
