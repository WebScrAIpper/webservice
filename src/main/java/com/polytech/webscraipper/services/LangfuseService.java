package com.polytech.webscraipper.services;

import org.springframework.beans.factory.annotation.Value;
import java.util.Map;
import java.util.Base64;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class LangfuseService {
    private final WebClient webClient;

    private final String sessionId = UUID.randomUUID().toString();

    public LangfuseService(@Value("${langfuse.api.username}") String username,
                           @Value("${langfuse.api.password}") String apiKey) {
        // Encodage en Base64 pour Basic Auth
        String authHeader = "Basic " + Base64.getEncoder().encodeToString((username + ":" + apiKey).getBytes());

        this.webClient = WebClient.builder()
                .baseUrl("https://cloud.langfuse.com/api/public")
                .defaultHeader("Authorization", authHeader)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public Mono<String> logLLMRequest(String url, String request, String response) {
        return webClient.post()
                .uri("/traces")
                .bodyValue(Map.of(
                        "name", "LLM Request",
                        "url", url,
                        "sessionId", sessionId,
                        "input", request,
                        "output", response
                ))
                .retrieve()
                .bodyToMono(String.class);
    }
}