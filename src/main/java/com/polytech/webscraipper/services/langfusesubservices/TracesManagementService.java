package com.polytech.webscraipper.services.langfusesubservices;

import com.polytech.webscraipper.sdk.LangfuseSDK;
import com.polytech.webscraipper.sdk.responses.PromptResponse;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TracesManagementService {

  @Autowired private LangfuseSDK langfuseSDK;

  /**
   * Post a trace to the Langfuse API
   *
   * @return the id of the trace
   */
  public String postGenericAILog(
      PromptResponse promptResponse, String response, String sourceUrl, String sessionId) {
    Map<String, Object> traceRequest =
        Map.of(
            "name",
            "Success",
            "promptName",
            promptResponse.name,
            "promptVersion",
            promptResponse.version,
            "response",
            response,
            "sourceUrl",
            sourceUrl,
            "sessionId",
            sessionId);
    String res = langfuseSDK.traces.postTrace(traceRequest);

    return extractIdFromResponse(res);
  }

  public String postFailedOutputAILog(
      PromptResponse promptResponse, String output, String sourceUrl, String sessionId) {
    Map<String, Object> traceRequest =
        Map.of(
            "name",
            "Failed",
            "promptName",
            promptResponse.name,
            "promptVersion",
            promptResponse.version,
            "response",
            output,
            "sourceUrl",
            sourceUrl,
            "sessionId",
            sessionId);
    String res = langfuseSDK.traces.postTrace(traceRequest);
    return extractIdFromResponse(res);
  }

  private String extractIdFromResponse(String res) {
    return res.substring("{\"id\":\"".length(), res.length() - 2);
  }
}
