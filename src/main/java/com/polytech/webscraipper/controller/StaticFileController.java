package com.polytech.webscraipper.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@RestController
@RequestMapping("/dynamic_pages")
public class StaticFileController {

    private static final String BASE_DIR = "./var/www/html/dynamic_pages/";

    @GetMapping("/{id}")
    public ResponseEntity<Resource> serveFile(@PathVariable String id) {
        File file = new File(BASE_DIR + id ); // Look for {id}.html
        
        if (!file.exists() || file.isDirectory()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                .body(resource);
    }
}
