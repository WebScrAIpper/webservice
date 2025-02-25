package com.polytech.webscraipper.sdk;

import com.polytech.webscraipper.BaseLogger;
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
      String userId) {
    var res =
        tracesClient.postTrace(
            Map.of(
                "input", input,
                "output", output,
                "sessionId", sessionId,
                "metadata", metadata,
                "tags", tags,
                "userId", userId));
    logger.info(
        "Posted a trace with id: "
            + "https://cloud.langfuse.com/project/cm6hy97qq06qy2y0ih8hh7ha2/traces/"
            + extractIdFromResponse(res));

    return extractIdFromResponse(res);
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
