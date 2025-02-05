package com.polytech.webscraipper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polytech.webscraipper.PromptException;
import com.polytech.webscraipper.dto.DocumentDto;
import com.polytech.webscraipper.services.DocumentService;
import java.io.IOException;
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
  public ResponseEntity<String> buildWebsiteSummary(
      @RequestParam String url, @RequestBody String content) throws IOException {
    return buildDocumentSummary(url, content, false); // false for website
  }

  @CrossOrigin(origins = "*")
  @PostMapping("/youtubeBuild")
  public ResponseEntity<String> buildYoutubeVodSummary(@RequestParam String url)
      throws IOException {
    return buildDocumentSummary(url, null, true); // true for YouTube
  }

  private ResponseEntity<String> buildDocumentSummary(
      String url, String content, boolean isYoutube) {
    if (url == null || url.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("The 'url' parameter is required and cannot be empty.");
    }

    if (documentService.getDocumentByUrl(url).isPresent()) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
          .body("A document with this URL already exists.");
    }

    if (!isYoutube && (content == null || content.isEmpty())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("The 'content' parameter is required and cannot be empty.");
    }
    try {
      if (isYoutube) {
        var res = documentService.buildYoutubeVodSummary(url);
        return ResponseEntity.status(HttpStatus.OK)
            .body(
                "The document summary has been successfully built.\n"
                    + objectMapper.writeValueAsString(res));
      } else {
        var res = documentService.buildWebsiteSummary(url, content);
        return ResponseEntity.status(HttpStatus.OK)
            .body(
                "The document summary has been successfully built.\n"
                    + objectMapper.writeValueAsString(res));
      }
    } catch (PromptException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An error occurred while building the document summary.");
    } catch (Throwable e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An unexpected error occurred while building the document summary.");
    }
  }
}
