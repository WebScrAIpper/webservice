package com.polytech.webscraipper.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.polytech.webscraipper.PromptException;
import com.polytech.webscraipper.dto.AIFilledDocument;
import com.polytech.webscraipper.dto.DocumentDto;
import com.polytech.webscraipper.repositories.DocumentRepository;
import com.polytech.webscraipper.sdk.LangfuseSDK;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import kotlin.Pair;
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
  @Autowired private LangfuseSDK langfuseSDK;

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
    var prompt = generatePrompt(scrappedContent);

    // Requesting the AI
    var aiAnswer = requestToAi(prompt);
    return buildAnswer(aiAnswer, url);
  }

  public DocumentDto buildYoutubeVodSummary(String url) throws IOException, PromptException {

    // Scraping vod content (transcript & metas)
    String content = scrapYoutubeVod(url);

    // Generating the prompt dynamically
    var prompt = generateYoutubePrompt(content);

    // Requesting the AI
    var aiAnswer = requestToAi(prompt);
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

  public DocumentDto buildTheAiJson(String url, String content)
      throws IOException, PromptException {
    return buildTheAiJson(url, content, true);
  }

  public DocumentDto buildTheAiJson(String url, String content, boolean langfuseTracing)
      throws PromptException, IOException {
    System.out.println("Content size to be processed: " + content.length());
    content = scrapContent(content);
    System.out.println("Content size after scraping: " + content.length());

    // Generating the prompt dynamically
    var prompt = generatePrompt(content);

    // Requesting the AI
    var aiAnswer = requestToAi(prompt);

    if (aiAnswer == null) throw new IOException("The AI answer is null");
    ;

    if (langfuseTracing) {
      System.out.println("Sending Langfuse log...");
      var langResponse =
          langfuseSDK.traces.postTrace(
              new Pair<>("name", "LLM Request"),
              new Pair<>("url", url),
              new Pair<>("input", prompt),
              new Pair<>("output", objectMapper.writeValueAsString(aiAnswer)),
              new Pair<>("sessionId", SESSION_ID));
      System.out.println(
          "Langfuse trace https://cloud.langfuse.com/project/cm6hy97qq06qy2y0ih8hh7ha2/traces/"
              + langResponse
                  .stripIndent()
                  .substring("{\"id\":\"".length(), langResponse.length() - 2)
              + " sent.\nWarning: The serveur update might take a few minutes");
    }

    // Building the response
    return new DocumentDto(aiAnswer, url);
  }

  public String generatePrompt(String content) {
    try {
      List<String> exampleInputLines =
          Files.readAllLines(Paths.get("src/main/resources/static/pageExample.html"));
      String inputExample = String.join("\n", exampleInputLines);

      String resultExample =
          """
                    {
                        "title": "Write code that is easy to delete, not easy to extend",
                        "author": "programming is terrible",
                        "date": "2016-02-13",
                        "image": null,
                        "description": "This document discusses the importance of writing disposable code to reduce maintenance costs, emphasizing practices like intentional code duplication to minimize dependencies and the strategic layering and separation of code components.",
                        "content_type": "ARTICLE",
                        "language": "ENGLISH",
                        "classifiers": ["Software Architecture", "Design Patterns"]
                    }
                    """
              .stripIndent();

      return """
                     Your role is to extract most important information's from a webpage.
                    \s
                     For instance, given the following HTML content:
                     ```html
                     %s
                     ```
                    \s
                     You should return a JSON object with the following structure:
                     ```json
                     %s
                     ```
                    \s
                     YOu should ALWAYS write the content of your summary in English even if the original content is in another language.
                    \s
                     The exact structure required by the json object is this one:
                     - title: the title of the document
                     - author: the author of the document
                     - date: the date of the document
                     - image: the image that best represents the document. The image should be a URL, if there is no image, on the website, it might be null.
                     - description: a short description of the document
                     - content_type: the type of content (can take the values ARTICLE | VIDEO | AUDIO)
                     - language: the language of the document
                     - classifiers: a list of topics that the document covers. Can go anywhere from 3 to 6. It's recommended for most of the classifiers of this list: %s. However, you may use other classifiers if you think they are more relevant. You should anyway never use a classifier that is not related to the topic of the document.
                    \s
                    \s
                    Here is the webpage you have to scrape:
                    ```html
                    %s
                    ```
                    \s
                    Do not wrap the response in a
                    ```json
                    ...
                    ```
                       block, just return the JSON object.
                    \s"""
          .stripIndent()
          .formatted(inputExample, resultExample, classifierService.getAllClassifiers(), content);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String generateYoutubePrompt(String content) {
    String resultExample =
        """
            {
                "title": "Write code that is easy to delete, not easy to extend",
                "author": "programming is terrible",
                "date": "2016-02-13",
                "image": null,
                "description": "This document discusses the importance of writing disposable code to reduce maintenance costs, emphasizing practices like intentional code duplication to minimize dependencies and the strategic layering and separation of code components.",
                "content_type": "DOCUMENT",
                "language": "ENGLISH",
                "classifiers": ["Software Architecture", "Design Patterns"]
            }
            """
            .stripIndent();

    return """
        Your role is to extract most important information's from a youtube video.

       \s
        You should return a JSON object with the following structure:
        ```json
        %s
        ```
       \s
        YOu should ALWAYS write the content of your summary in English even if the original content is in another language.
       \s
        The exact structure required by the json object is this one:
        - title: the title of the document, in the language of the document.
        - author: the author of the document
        - date: the date of the document
        - image: the image that best represents the document. The image should be a URL, if there is no image, on the website, it might be null.
        - description: a short description of the document
        - content_type: the type of content (can take the values ARTICLE | VIDEO | AUDIO)
        - language: the language of the document, not the language of the summary you are making.
        - classifiers: a list of topics that the document covers. Can go anywhere from 3 to 6. It's recommended for most of the classifiers of this list: %s. However, you may use other classifiers if you think they are more relevant. You should anyway never use a classifier that is not related to the topic of the document.
       \s
       \s
       Here is the informations about the video that we collected on the youtube video page and the transcript you have to summarize:

       %s

       \s
       Do not wrap the response in a
       ```json
       ...
       ```
          block, just return the JSON object.
       \s"""
        .stripIndent()
        .formatted(resultExample, classifierService.getAllClassifiers(), content);
  }

  public AIFilledDocument requestToAi(String prompt) throws PromptException {

    int MAX_TRIES = 3;
    int tries = 0;

    while (tries < MAX_TRIES) {
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
        tries++;
        System.out.println("Error while calling the AI: " + e.getMessage());
      }
    }
    throw new PromptException("The LLM did not succeed to fill the summary.");
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
