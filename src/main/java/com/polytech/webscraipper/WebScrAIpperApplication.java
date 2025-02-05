package com.polytech.webscraipper;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableFeignClients
public class WebScrAIpperApplication {

    public WebScrAIpperApplication() {}

    public static void main(String[] args) {
        SpringApplication.run(WebScrAIpperApplication.class, args);
    }
}
