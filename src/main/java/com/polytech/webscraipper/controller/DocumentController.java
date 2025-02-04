package com.polytech.webscraipper.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.polytech.webscraipper.dto.DocumentDto;
import com.polytech.webscraipper.services.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/api")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    public DocumentController() {
    }

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
            @RequestParam String url,
            @RequestBody String content) {
        return buildDocumentSummary(url, content, false); // false for website
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/youtubeBuild")
    public ResponseEntity<String> buildYoutubeVodSummary(
            @RequestParam String url) {
        return buildDocumentSummary(url, null, true); // true for YouTube
    }

    private ResponseEntity<String> buildDocumentSummary(String url, String content, boolean isYoutube) {
        if (url == null || url.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("The 'url' parameter is required and cannot be empty.");
        }

        if (documentService.getDocumentByUrl(url).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("A document with this URL already exists.");
        }

        if (!isYoutube && (content == null || content.isEmpty())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("The 'content' parameter is required and cannot be empty.");
        }

        if (isYoutube) {
            return documentService.buildYoutubeVodSummary(url);
        } else {
           return documentService.buildWebsiteSummary(url, content);
        }
    }

}
