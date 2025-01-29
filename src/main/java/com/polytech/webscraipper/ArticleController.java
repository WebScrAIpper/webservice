package com.polytech.webscraipper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.polytech.webscraipper.dto.AIFilledArticle;
import com.polytech.webscraipper.dto.ArticleDto;
import com.polytech.webscraipper.dto.ClassifierDto;
import com.polytech.webscraipper.repositories.ArticleRepository;
import com.polytech.webscraipper.repositories.ClassifierRepository;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


@RestController
@RequestMapping("/api")
public class ArticleController
{
    private final ChatModel chatModel;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ArticleRepository articleRepo;
    @Autowired
    private ClassifierRepository classifierRepository;


    public ArticleController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/articles")
    public List<ArticleDto> getArticles() {
        return articleRepo.findAll();
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/build")
    public ResponseEntity<String> buildAWebSiteResume(
            @RequestParam String url,
            @RequestBody String content
    ) throws JsonProcessingException {

        if (url == null || url.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("The 'url' parameter is required and cannot be empty.");
        }
        if (content == null || content.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("The 'content' parameter is required and cannot be empty.");
        }

        System.out.println("Content size to be processed: " + content.length());
        content = scrapContent(content);
        System.out.println("Content size after scraping: " + content.length());

        var prompt = generatePrompt(content);
        var aiAnswer = requestToAi(prompt);

        if (aiAnswer == null) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the AI request. Please try again later.");
        }
        ArticleDto articleDto = new ArticleDto(aiAnswer, url);
        String answer = objectMapper.writeValueAsString(articleDto);

        // Handle Classifiers
        var newClassifiers = Arrays.stream(aiAnswer.getClassifiers()).filter(classifier -> !getExistingClassifiers().contains(classifier)).toArray(String[]::new);
        for (String newClassifier : newClassifiers) {
            addClassifier(newClassifier);
        }

        articleRepo.save(articleDto);
        return ResponseEntity.ok(answer);
    }

    @GetMapping("/classifiers")
    public List<String> getExistingClassifiers() {
        return classifierRepository.findAll().stream().map(ClassifierDto::getName).toList();
    }

    @PostMapping("/classifiers/add/{classifier}")
    public ResponseEntity<String> addClassifier(@PathVariable String classifier) {
        if (classifier == null || classifier.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("The 'classifier' parameter is required and cannot be empty.");
        }
        if (classifierRepository.findAll().contains(new ClassifierDto(classifier))) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("The classifier already exists.");
        }
        classifierRepository.save(new ClassifierDto(classifier));
        return ResponseEntity.ok("Classifier added successfully.");
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
                    "description": "This article discusses the importance of writing disposable code to reduce maintenance costs, emphasizing practices like intentional code duplication to minimize dependencies and the strategic layering and separation of code components.",
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
            - title: the title of the article
            - author: the author of the article
            - date: the date of the article
            - image: the image that best represents the article (not required for now)
            - description: a short description of the article
            - content_type: the type of content (can take the values ARTICLE | VIDEO | AUDIO)
            - language: the language of the article
            - classifiers: a list of topics that the article covers. You have to use at most 1 new classifier and have to choose the other classifiers from this list: %s
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
           \s""".stripIndent().formatted(inputExample, resultExample, getExistingClassifiers(), content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public AIFilledArticle requestToAi(String prompt) {
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
                // Log
                System.out.println(aiAnswer.getResult().getOutput().getText());
                return objectMapper.readValue(aiAnswer.getResult().getOutput().getText(), AIFilledArticle.class);

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
