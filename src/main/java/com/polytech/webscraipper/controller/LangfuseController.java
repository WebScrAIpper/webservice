package com.polytech.webscraipper.controller;

import com.polytech.webscraipper.services.LangfuseService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class LangfuseController {

    private final LangfuseService langfuseService;

    public LangfuseController(LangfuseService langfuseService) {
        this.langfuseService = langfuseService;
    }

    // Endpoint pour tester l'envoi de logs vers Langfuse
    @PostMapping("/log")
    public Mono<String> logRequest(@RequestBody Map<String, String> payload) {
        System.out.println("Received log request: " + payload);
        String url = payload.get("url");
        String input = payload.get("input");
        String output = payload.get("output");

        return langfuseService.logLLMRequest(url, input, output)
                .map(res -> "Log envoyé à Langfuse !");
    }
}