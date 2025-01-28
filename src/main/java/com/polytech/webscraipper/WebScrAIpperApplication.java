package com.polytech.webscraipper;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class WebScrAIpperApplication {
    private final ChatModel chatModel;

    public WebScrAIpperApplication(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public static void main(String[] args) {
        SpringApplication.run(WebScrAIpperApplication.class, args);
    }
}
