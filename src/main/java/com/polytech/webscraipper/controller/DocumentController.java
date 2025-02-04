package com.polytech.webscraipper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polytech.webscraipper.dto.DocumentDto;
import com.polytech.webscraipper.repositories.DocumentRepository;
import com.polytech.webscraipper.services.DocumentService;
import com.polytech.webscraipper.services.ClassifierService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;

@RestController
@RequestMapping("/api")
public class DocumentController
{
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private DocumentService documentService;
    @Autowired
    private ClassifierService classifierService;
    @Autowired
    private DocumentRepository documentRepo;


    public DocumentController() {}

    @GetMapping("/documents")
    public List<DocumentDto> getDocuments() {
        return documentRepo.findAll();
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/build")
    public ResponseEntity<String> buildAWebSiteResume(
            @RequestParam String url,
            @RequestBody String content
    ) {
        if (url == null || url.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("The 'url' parameter is required and cannot be empty.");
        }
        if (content == null || content.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("The 'content' parameter is required and cannot be empty.");
        }

        // Building the AI JSON
        try {
            DocumentDto documentDto = documentService.buildTheAiJson(url, content);

            String answer = objectMapper.writeValueAsString(documentDto);

            // Handle Classifiers
            var newClassifiers = Arrays.stream(documentDto.getClassifiers()).filter(classifier -> !classifierService.getAllClassifiers().contains(classifier)).toArray(String[]::new);
            for (String newClassifier : newClassifiers) {
                classifierService.addClassifier(newClassifier);
            }

            // Saving the document
            documentRepo.save(documentDto);

            return ResponseEntity.ok(answer);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the content: " + e.getMessage());
        }
    }


}
