package com.polytech.webscraipper.sdk;

import com.polytech.webscraipper.BaseLogger;
import com.polytech.webscraipper.sdk.responses.TracesResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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
      String name) {
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

  /**
   * Get all traces with optional filters. The limit does not exceed 100.
   *
   * @param page the page number
   * @param limit the maximum number of traces to return
   * @param userId the user id to filter traces
   * @param sessionId the session id to filter traces
   * @return a list of traces
   */
  public List<Map<String, Object>> getAllTraces(
      Integer page, Integer limit, String userId, String sessionId) {
    Map<String, Object> queryParams = new HashMap<>();
    if (page != null) {
      queryParams.put("page", page);
    }
    if (limit != null) {
      queryParams.put("limit", limit);
    }
    if (userId != null) {
      queryParams.put("userId", userId);
    }
    if (sessionId != null) {
      queryParams.put("sessionId", sessionId);
    }

    TracesResponse response = tracesClient.getAllTraces(queryParams);
    return response != null ? response.getData() : Collections.emptyList();
  }

  public int getTotalTraces(String userId, String sessionId) {
    int totalTraces = 0;
    int page = 1;
    int pageSize = 100; // API max limit

    while (true) {
      Map<String, Object> queryParams = new HashMap<>();
      queryParams.put("page", page);
      queryParams.put("limit", pageSize);
      if (userId != null) {
        queryParams.put("userId", userId);
      }
      if (sessionId != null) {
        queryParams.put("sessionId", sessionId);
      }

      TracesResponse response = tracesClient.getAllTraces(queryParams);
      if (response == null || response.getData().isEmpty()) {
        break;
      }

      totalTraces += response.getData().size();

      // Stop fetching if there are no more pages
      if (response.getMeta() == null || page >= response.getMeta().getTotalPages()) {
        break;
      }

      page++;
    }
    return totalTraces;
  }

  @FeignClient(
      name = "langfuse-sdk-traces",
      url = "https://cloud.langfuse.com/api/public/traces",
      configuration = FeignConfig.class)
  public interface TracesClient {
    @PostMapping("/")
    String postTrace(@RequestBody Map<String, Object> traceRequest);

    @GetMapping("/")
    TracesResponse getAllTraces(@RequestParam Map<String, Object> queryParams);
  }

  private String extractIdFromResponse(String res) {
    return res.substring("{\"id\":\"".length(), res.length() - 2);
  }
}
