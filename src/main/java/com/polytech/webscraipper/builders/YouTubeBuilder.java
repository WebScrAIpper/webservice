package com.polytech.webscraipper.builders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.polytech.webscraipper.dto.DocumentDto;
import com.polytech.webscraipper.exceptions.PromptException;
import com.polytech.webscraipper.exceptions.ScrappingException;
import com.polytech.webscraipper.sdk.responses.PromptResponse;
import com.polytech.webscraipper.services.langfusesubservices.PromptManagementService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// TODO
@Component
public class YouTubeBuilder implements ISummaryBuilder {

  @Autowired PromptManagementService promptManagementService;
  @Autowired private ObjectMapper objectMapper;

  @Override
  public String scrapContent(String url, String pageContent) throws ScrappingException {
    try {
      // Get vod metadata
      String videoInfoJson = executePythonScript("src/scripts/get_yt_infos.py", url);

      // Get transcript
      String transcript = executePythonScript("src/scripts/get_transcript.py", url);

      Map<String, String> result = new HashMap<>();
      result.put("metadata", videoInfoJson);
      result.put("transcript", transcript);

      Gson gson = new Gson();
      return objectMapper.writeValueAsString(result);
    } catch (IOException | InterruptedException e) {
      throw new ScrappingException(
          "Error while scrapping the content of the website:" + e.getMessage());
    }
  }

  @Override
  public PromptResponse generatePrompt(String scrappedContent, List<String> classifiers) {
    return null;
  }

  @Override
  public DocumentDto polishAnswer(String url, DocumentDto response) throws PromptException {
    return null;
  }

  @Override
  public boolean isAnAppropriateBuilder(String url) {
    return false;
  }

  // TODO: think about moving this method somewhere else
  public String executePythonScript(String scriptPath, String url)
      throws IOException, InterruptedException {

    ProcessBuilder pb;
    if (System.getProperty("os.name").contains("Windows")) {
      pb = new ProcessBuilder("src/.venv/Scripts/python.exe", scriptPath, url);
    } else {
      pb = new ProcessBuilder("src/.venv/bin/python3", scriptPath, url);
    }
    Process process = pb.start();

    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    StringBuilder output = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
      output.append(line).append("\n");
    }

    int exitCode = process.waitFor();

    if (exitCode != 0) {
      BufferedReader errorReader =
          new BufferedReader(new InputStreamReader(process.getErrorStream()));
      StringBuilder errorOutput = new StringBuilder();
      while ((line = errorReader.readLine()) != null) {
        errorOutput.append(line).append("\n");
      }
      throw new IOException(
          "Error executing Python script " + scriptPath + " : " + errorOutput.toString().trim());
    }

    return output.toString().trim();
  }
}
