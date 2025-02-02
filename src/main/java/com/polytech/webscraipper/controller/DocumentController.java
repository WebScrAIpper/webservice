package com.polytech.webscraipper.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.polytech.webscraipper.dto.DocumentDto;
import com.polytech.webscraipper.repositories.DocumentRepository;
import com.polytech.webscraipper.services.DocumentService;
import com.polytech.webscraipper.services.ClassifierService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;

@RestController
@RequestMapping("/api")
public class DocumentController {
    private final ObjectMapper objectMapper = new ObjectMapper();

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
        return documentService.getDocumentByUrl(url);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/build")
    public ResponseEntity<String> buildWebsiteSummary(
            @RequestParam String url,
            @RequestBody String content) throws JsonProcessingException {

        documentService.buildWebsiteSummary(url, content);
        return ResponseEntity.ok("Website summary successfully built.");
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/youtubeBuild")
    public ResponseEntity<String> buildYoutubeVodSummary(
            @RequestParam String url) throws JsonProcessingException {
        documentService.buildYoutubeVodSummary(url);
        return ResponseEntity.ok("Youtube VOD summary successfully built.");
    }
}
