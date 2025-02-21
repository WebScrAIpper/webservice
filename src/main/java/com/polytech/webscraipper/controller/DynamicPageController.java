package com.polytech.webscraipper.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dynamic_pages")
public class DynamicPageController {

  private final SummaryController summaryController;

  public DynamicPageController(SummaryController summaryController) {
    this.summaryController = summaryController;
  }

  @GetMapping("/{id}")
  public ResponseEntity<String> serveCachedSummary(@PathVariable String id) {
    String htmlContent = summaryController.summaryCache.getIfPresent(id);

    if (htmlContent == null) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok().header("Content-Type", "text/html").body(htmlContent);
  }
}
