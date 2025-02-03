package com.polytech.webscraipper.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polytech.webscraipper.dto.AIFilledDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DocumentService {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ClassifierService classifierService;
    @Autowired
    private ChatModel chatModel;

    public DocumentService() {
    }

    public String generatePrompt(
            String content,
            String url
    ) {
        if (isYoutubeVideo(url)){
            return generateYoutubePrompt(content);
        }
        try {
            List<String> exampleInputLines = Files
                    .readAllLines(Paths.get("src/main/resources/static/pageExample.html"));
            String inputExample = String.join("\n", exampleInputLines);

            String resultExample = """
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
        String resultExample = """
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
            """.stripIndent();

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
       \s""".stripIndent().formatted(resultExample, classifierService.getAllClassifiers(), content);
    }

    public AIFilledDocument requestToAi(String prompt) throws Exception {

        int MAX_TRIES = 3;
        int tries = 0;

        while (tries < MAX_TRIES) {
            try {
                var aiAnswer = chatModel.call(
                        new Prompt(
                                prompt,
                                OpenAiChatOptions.builder()
                                        .model("gpt-4o-mini")
                                        .build()));

                String aiResponseText = aiAnswer.getResult().getOutput().getText();
                if (aiResponseText == null || aiResponseText.isEmpty()) {
                    throw new IllegalArgumentException("AI returned an empty or invalid response.");
                }

                System.out.println(aiResponseText);
                return objectMapper.readValue(aiResponseText, AIFilledDocument.class);

            } catch (IOException e) {
                tries++;
                if (tries >= MAX_TRIES) {
                    throw new IOException("Failed to contact AI service after multiple attempts.");
                }
                System.out.println("An error occurred while processing the AI request. Retrying...");
            } catch (Exception e) {
                throw new RuntimeException("An unexpected error occurred while processing the AI request.");
            }
        }
        throw new IOException("Could not retrieve a valid response from AI after multiple attempts.");
    }

    public String scrapContent(String content,String url) {
        if (isYoutubeVideo(url)){
            return getYoutubeContent(url);
        }
        Document document = Jsoup.parse(content);

        document.select("script, style, form, nav, aside, button, svg").remove();
        // TODO: think about the iframe
        return document.text();
    }

    public boolean isYoutubeVideo(String videoUrl) {
        return videoUrl != null && videoUrl.startsWith("https://www.youtube.com/watch?");
    }

    private String getYoutubeContent(String url) {
        try {
            //Get video information
            String videoInfoJson = executeScript("src/scripts/get_yt_infos.py", url);

            //Get transcript
            String transcript = executeScript("src/scripts/get_transcript.py", url);

            Map<String, String> result = new HashMap<>();
            result.put("infos", videoInfoJson);
            result.put("transcript", transcript);

            Gson gson = new Gson();
            return gson.toJson(result);

        } catch (Exception e) {
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    private String executeScript(String scriptPath, String url) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("src/.venv/bin/python3", scriptPath, url);        Process process = pb.start();

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
