package com.polytech.webscraipper.controller;

import com.polytech.webscraipper.services.ClassifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
public class ClassifierController
{

    @Autowired
    private ClassifierService classifierService;


    public ClassifierController() {
    }

    @GetMapping("/classifiers")
    public List<String> getExistingClassifiers() {
        return classifierService.getAllClassifiers();
    }

    @PostMapping("/classifiers/add/{classifier}")
    public ResponseEntity<String> addClassifier(@PathVariable String classifier) {
        if (classifier == null || classifier.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("The 'classifier' parameter is required and cannot be empty.");
        }
        var result = classifierService.addClassifier(classifier);
        if (!result) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("The classifier already exists.");
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("The classifier has been added.");
    }

}
