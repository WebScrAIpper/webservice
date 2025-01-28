package com.polytech.webscraipper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@SpringBootTest
class WebScrAIpperApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void testWebSiteScrapping() throws IOException {
        var url = "https://eev.ee/blog/2016/07/26/the-hardest-problem-in-computer-science/";
        List<String> lines = Files.readAllLines(Paths.get("src/test/resources/pageToSummarize.html"));
        String content = String.join("\n", lines);
        //... to finish

    }

}
