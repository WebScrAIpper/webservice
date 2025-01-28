package com.polytech.webscraipper;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@SpringBootApplication
@RestController
public class WebScrAIpperApplication {

    private final ChatModel chatModel;

    public WebScrAIpperApplication(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public static void main(String[] args) {
        SpringApplication.run(WebScrAIpperApplication.class, args);
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        return chatModel.call(
                new Prompt(
                        "Say a customised hello to the user named " + name,
                        OpenAiChatOptions.builder()
                                .model("gpt-4o-mini")
                                .build()
                )
        ).getResult().getOutput().getText();
    }
    public String generatePrompt(
            String pathOfTheContentToScrape
    ) throws IOException {

        List<String> contentLines = Files.readAllLines(Paths.get(pathOfTheContentToScrape));
        String content = String.join("\n", contentLines);


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
                    "classifiers": ["Software Architecture", "Design Patterns"]
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
            - content_type: the type of content (can take the values ARTICLE | VIDEO | PODCAST)
            - language: the language of the article
            - classifiers: a list of topics that the article covers
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
           \s""".stripIndent().formatted(inputExample, resultExample, content);
    }


    @GetMapping("/prompt")
    public String prompt() throws IOException {
        return chatModel.call(
                new Prompt(
                        generatePrompt("src/main/resources/static/pageToSummarize.html"),
                        OpenAiChatOptions.builder()
                                .model("gpt-4o-mini")
                                .build()
                )
        ).getResult().getOutput().getText();
    }
}
