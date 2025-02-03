package com.polytech.webscraipper.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polytech.webscraipper.dto.AIFilledDocument;
import com.polytech.webscraipper.dto.DocumentDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Service
public class DocumentService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ClassifierService classifierService;
    @Autowired
    private ChatModel chatModel;
    @Autowired
    private LangfuseService langfuseService;

    public DocumentService() {
    }

    public DocumentDto buildTheAiJson(String url, String content) throws IOException {
        return buildTheAiJson(url, content, true);
    }

    public DocumentDto buildTheAiJson(String url, String content, boolean langfuseTracing) throws IOException {
        System.out.println("Content size to be processed: " + content.length());
        content = scrapContent(content);
        System.out.println("Content size after scraping: " + content.length());

        // Generating the prompt dynamically
        var prompt = generatePrompt(content);

        // Requesting the AI
        var aiAnswer = requestToAi(prompt);

        if (aiAnswer == null)
            throw new IOException("The AI answer is null");;

        if (langfuseTracing) {
            System.out.println("Sending Langfuse log...");
            var langResponse = langfuseService.logLLMRequest(url, prompt, objectMapper.writeValueAsString(aiAnswer));
            if (langResponse.block() != null && Objects.requireNonNull(langResponse.block()).startsWith("{\"id\":\"")) {
                System.out.println("Langfuse log sent successfully");
            } else {
                System.out.println("An error occurred while sending the Langfuse log");
                System.out.println("Response : " + langResponse.block());
            }
        }

        // Building the response
        return new DocumentDto(aiAnswer, url);
    }

    public String generatePrompt(
            String content
    ) {
        try {
            List<String> exampleInputLines = Files.readAllLines(Paths.get("src/main/resources/static/pageExample.html"));
            String inputExample = String.join("\n", exampleInputLines);

            String resultExample = """
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
                """.stripIndent();

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
           \s""".stripIndent().formatted(inputExample, resultExample, classifierService.getAllClassifiers(), content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public AIFilledDocument requestToAi(String prompt) {
        int MAX_TRIES = 3;
        int tries = 0;

        while (tries < MAX_TRIES) {
            try {
                var aiAnswer = chatModel.call(
                        new Prompt(
                                prompt,
                                OpenAiChatOptions.builder()
                                        .model("gpt-4o-mini")
                                        .build()
                        )
                );
                System.out.println(aiAnswer.getResult().getOutput().getText());
                return objectMapper.readValue(aiAnswer.getResult().getOutput().getText(), AIFilledDocument.class);

            } catch (IOException e) {
                System.out.println("An error occurred while processing the AI request. Retrying...");
                System.out.println(e.getMessage());
                tries++;
            }
        }
        return null;
    }

    public String scrapContent(String content) {
        Document document = Jsoup.parse(content);

        document.select("script, style, form, nav, aside, button, svg").remove();
        //TODO: think about the iframe
        return document.text();
    }
}
