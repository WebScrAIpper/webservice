package com.polytech.webscraipper.sdk;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public class Traces {
  private final TracesClient tracesClient;

  public Traces(TracesClient tracesClient) {
    this.tracesClient = tracesClient;
  }

  public String postTrace(Map<String, Object> traceData) {
    return tracesClient.postTrace(traceData);
  }

  @FeignClient(
      name = "langfuse-sdk-traces",
      url = "https://cloud.langfuse.com/api/public/traces",
      configuration = FeignConfig.class)
  public interface TracesClient {
    @PostMapping("/")
    String postTrace(@RequestBody Map<String, Object> traceRequest);
  }
}
