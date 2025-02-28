package com.polytech.webscraipper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polytech.webscraipper.BaseLogger;
import com.polytech.webscraipper.builders.DefaultBuilder;
import com.polytech.webscraipper.exceptions.DocumentException;
import com.polytech.webscraipper.models.Document;
import com.polytech.webscraipper.services.DocumentService;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class DocumentController {

  @Autowired private DocumentService documentService;
  @Autowired private ObjectMapper objectMapper;

  private final BaseLogger logger = new BaseLogger(DefaultBuilder.class);

  public DocumentController() {}

  @GetMapping("/documents")
  public List<Document> getAllDocuments() {
    return documentService.getAllDocuments();
  }

  @GetMapping("/document")
  public Optional<Document> getDocumentByURL(@RequestParam String url) {
    Optional<Document> document = documentService.getDocumentByUrl(url);
    if (document.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document Not Found");
    }
    return document;
  }

  @CrossOrigin(origins = "*")
  @PostMapping("/build")
  public ResponseEntity<String> buildWebsiteSummary(
      @RequestParam String url, @RequestBody String content) throws IOException {
    url = URLDecoder.decode(url, StandardCharsets.UTF_8);
    logger.info("Building document summary for " + url);
    var res = buildDocumentSummary(url, content); // false for website
    return res;
  }

  private ResponseEntity<String> buildDocumentSummary(String url, String content) {
    if (url == null || url.isEmpty()) {
      logger.error("Tried to build document summary without URL.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("The 'url' parameter is required and cannot be empty.");
    }

    if (documentService.getDocumentByUrl(url).isPresent()) {
      logger.error("Tried to build document summary for an already existing document.");
      return ResponseEntity.status(HttpStatus.CONFLICT)
          .body("A document with this URL already exists.");
    }

    try {
      var res = documentService.buildWebsiteSummary(url, content);
      return ResponseEntity.status(HttpStatus.OK)
          .body(
              "The document summary has been successfully built.\n"
                  + objectMapper.writeValueAsString(res));
    } catch (DocumentException e) {
      logger.error("An error occurred while building the document summary.", e);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (Throwable e) {
      logger.error("An unexpected error occurred while building the document summary.", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An unexpected error occurred while building the document summary.");
    }
  }
}
