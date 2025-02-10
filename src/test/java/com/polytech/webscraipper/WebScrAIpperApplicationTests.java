package com.polytech.webscraipper;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.polytech.webscraipper.dto.DocumentDto;
import com.polytech.webscraipper.services.DocumentService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WebScrAIpperApplicationTests {

  @Autowired private DocumentService documentService;

  @Test
  void buildWebsiteSummaryTest() {
    List<String> contents = new ArrayList<>();
    Path directoryPath = Paths.get("src/test/resources");

    try (Stream<Path> paths = Files.list(directoryPath)) {
      paths
          .filter(path -> path.getFileName().toString().matches("test\\d+\\.html"))
          .sorted()
          .forEach(
              path -> {
                try {
                  String content = new String(Files.readAllBytes(path));
                  contents.add(content);
                } catch (IOException e) {
                  e.printStackTrace();
                }
              });
    } catch (IOException e) {
      e.printStackTrace();
    }

    for (int i = 0; i < contents.size(); i++) {
      try {
        long startTime = System.nanoTime();
        DocumentDto response =
            documentService.buildWebsiteSummary(Integer.toString(i), contents.get(i), false);
        long duration = System.nanoTime() - startTime;
        System.out.println("Time taken for test i: " + (duration / 1_000_000) + " ms");
        assertTrue(response != null);
      } catch (IOException e) {
        System.out.println("IOException: " + e.getMessage());
        fail();
      } catch (PromptException e) {
        System.out.println("PromptException: " + e.getMessage());
        fail();
      }
    }
  }
}
