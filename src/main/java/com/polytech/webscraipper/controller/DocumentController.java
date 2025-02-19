package com.polytech.webscraipper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polytech.webscraipper.dto.DocumentDto;
import com.polytech.webscraipper.exceptions.PromptException;
import com.polytech.webscraipper.services.DocumentService;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
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

  public DocumentController() {}

  @GetMapping("/documents")
  public List<DocumentDto> getAllDocuments() {
    return documentService.getAllDocuments();
  }

  @GetMapping("/document")
  public Optional<DocumentDto> getDocumentByURL(@RequestParam String url) {
    Optional<DocumentDto> document = documentService.getDocumentByUrl(url);
    if (document.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document Not Found");
    }
    return document;
  }

  @CrossOrigin(origins = "*")
  @PostMapping("/build")
  public ResponseEntity<String> buildWebsiteSummary(@RequestParam String url, String content)
      throws IOException {
    url = URLDecoder.decode(url, StandardCharsets.UTF_8);
    System.out.println("Building document summary for " + url);
    var res = buildDocumentSummary(url, content); // false for website
    if (res.getStatusCode() != HttpStatus.OK) {
      System.out.println("Error while building document summary for " + url + "\n" + res.getBody());
    }
    return res;
  }

  private ResponseEntity<String> buildDocumentSummary(String url, String content) {
    if (url == null || url.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("The 'url' parameter is required and cannot be empty.");
    }

    if (documentService.getDocumentByUrl(url).isPresent()) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
          .body("A document with this URL already exists.");
    }

    try {
      var res = documentService.buildWebsiteSummary(url, content);
      return ResponseEntity.status(HttpStatus.OK)
          .body(
              "The document summary has been successfully built.\n"
                  + objectMapper.writeValueAsString(res));
    } catch (PromptException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (Throwable e) {
      System.out.println(
          "An unexpected error occurred while building the document summary.\n" + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An unexpected error occurred while building the document summary.");
    }
  }

  private static final String BASE_PATH = "./var/www/html/dynamic_pages/";

  @PostMapping("/pre-save-summary")
  public ResponseEntity<String> preSaveDocumentSummary(@RequestParam String url, @RequestBody String content) {
    if (url == null || url.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("The 'url' parameter is required and cannot be empty.");
    }

    try {
      // Générer un ID unique
      String uniqueId = UUID.randomUUID().toString();
      String htmlFilename = uniqueId + ".html";
      Path htmlPath = Path.of(BASE_PATH, htmlFilename);

      // Encoder le HTML pour affichage sans exécution
      String escapedContent = escapeHtml(content);
      String escapedUrl = escapeHtml(url);

      // Création du fichier HTML avec un script pour stocker les variables
      String htmlContent = "<!DOCTYPE html>\n<html>\n<head>\n<title>Document Summary</title>\n</head>\n<body>\n" +
          "<h1>Document Summary</h1>\n" +
          "<p>URL: " + escapedUrl + "</p>\n" +
          "<p>Content:</p>\n<pre>" + escapedContent + "</pre>\n" +
          // Here we store the variables in a JavaScript object
          "<script>\n" +
          "window.documentSummary = {\n" +
          "    url: " + "\"" + escapedUrl + "\",\n" +
          "    content: " + "\"" + escapedContent + "\"\n" +
          "};\n" +
          "</script>\n" +
          "</body>\n</html>";

      Files.writeString(htmlPath, htmlContent, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

      // Retourne l'URL de la page générée
      String pageUrl = "localhost:8080/dynamic_pages/" + htmlFilename;
      return ResponseEntity.status(HttpStatus.OK).body(pageUrl);

    } catch (IOException e) {
      System.out.println("Exception while creating the document summary page: " + e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Failed to create the document summary page: " + e.getMessage());
    }
  }

  /**
   * Échappe les caractères HTML pour empêcher leur interprétation et exécution.
   */
  private String escapeHtml(String input) {
    // Replace line breaks with the escape sequence for JavaScript newlines
    String escapedInput = input.replace("\n", "\\n");

    // Escape HTML special characters to prevent execution
    return escapedInput.replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#39;");
  }
}
