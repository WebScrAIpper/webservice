package com.polytech.webscraipper.controller;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.polytech.webscraipper.services.HtmlService;
import java.util.*;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pre-save-summary")
public class SummaryController {

  @Autowired private HtmlService htmlService;

  final Cache<String, String> summaryCache =
      Caffeine.newBuilder()
          .expireAfterWrite(10, TimeUnit.MINUTES) // Auto-remove after 10 minutes
          .maximumSize(500) // Limit cache size
          .build();

  @PostMapping
  public ResponseEntity<String> preSaveDocumentSummary(
      @RequestParam String url, @RequestBody String content) {
    if (url == null || url.isEmpty()) {
      return ResponseEntity.badRequest()
          .body("The 'url' parameter is required and cannot be empty.");
    }

    String uniqueId = UUID.randomUUID().toString();
    String htmlContent = htmlService.generateHtml(url, content);

    summaryCache.put(uniqueId, htmlContent);

    return ResponseEntity.ok("http://localhost:8080/dynamic_pages/" + uniqueId);
  }
}
