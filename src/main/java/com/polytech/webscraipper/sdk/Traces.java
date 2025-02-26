package com.polytech.webscraipper.sdk;

import com.polytech.webscraipper.BaseLogger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public class Traces {
  private final TracesClient tracesClient;

  private final BaseLogger logger = new BaseLogger(Traces.class);

  public Traces(TracesClient tracesClient) {
    this.tracesClient = tracesClient;
  }

  /**
   * Post a trace to the Langfuse API
   *
   * @param input the input
   * @param output the output
   * @param sessionId the session id
   * @param metadata the metadata
   * @param tags the tags
   * @param userId the user who made the request
   * @return the id of the trace
   */
  public String postTrace(
      String input,
      String output,
      String sessionId,
      Map<String, Object> metadata,
      List<String> tags,
      String userId,
      String name
      ) {
    Map<String, Object> reqMap = new HashMap<>();
    if (input != null) {
      reqMap.put("input", input);
    }
    if (output != null) {
      reqMap.put("output", output);
    }
    if (sessionId != null) {
      reqMap.put("sessionId", sessionId);
    }
    if (metadata != null) {
      reqMap.put("metadata", metadata);
    }
    if (tags != null) {
      reqMap.put("tags", tags);
    }
    if (userId != null) {
      reqMap.put("userId", userId);
    }
    if (name != null) {
      reqMap.put("name", name);
    }

    var res = tracesClient.postTrace(reqMap);
    logger.info(
        "Posted a trace with id: "
            + "https://cloud.langfuse.com/project/cm6hy97qq06qy2y0ih8hh7ha2/traces/"
            + extractIdFromResponse(res));

    return extractIdFromResponse(res);
  }

  public String postTrace(
      String input,
      String output,
      String sessionId,
      Map<String, Object> metadata,
      List<String> tags) {
    return postTrace(input, output, sessionId, metadata, tags, null, null);
  }

  public String postTrace(
      String input, String output, String sessionId, Map<String, Object> metadata) {
    return postTrace(input, output, sessionId, metadata, null, null, null);
  }

  public String postTrace(String input, String output, String sessionId) {
    return postTrace(input, output, sessionId, null, null, null, null);
  }

  public String postTrace(String input, String output) {
    return postTrace(input, output, null, null, null, null, null);
  }

  @FeignClient(
      name = "langfuse-sdk-traces",
      url = "https://cloud.langfuse.com/api/public/traces",
      configuration = FeignConfig.class)
  public interface TracesClient {
    @PostMapping("/")
    String postTrace(@RequestBody Map<String, Object> traceRequest);
  }

  private String extractIdFromResponse(String res) {
    return res.substring("{\"id\":\"".length(), res.length() - 2);
  }
}
