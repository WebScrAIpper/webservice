package com.polytech.webscraipper.services.langfusesubservices;

import com.polytech.webscraipper.sdk.LangfuseSDK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TracesManagementService {

    @Autowired
    private LangfuseSDK langfuseSDK;

    /**
     * Post a trace to the Langfuse API
     *
     * @return the id of the trace
     */
    public int postGenericAILog(
            String promptName,
            int promptVersion,
            String response,
            String sourceUrl,
            String sessionId
            ) {
        Map<String, Object> traceRequest = Map.of(
                "name", "Success",
                "promptName", promptName,
                "promptVersion", promptVersion,
                "response", response,
                "sourceUrl", sourceUrl,
                "sessionId", sessionId
                );
        String res = langfuseSDK.traces.postTrace(traceRequest);

        return extractIdFromResponse(res);
    }

    public int postFailedOutputAILog(
            String promptName,
            int promptVersion,
            String response,
            String sourceUrl,
            String sessionId
            ) {
        Map<String, Object> traceRequest = Map.of(
                "name", "Failed",
                "promptName", promptName,
                "promptVersion", promptVersion,
                "response", response,
                "sourceUrl", sourceUrl,
                "sessionId", sessionId
        );
        String res = langfuseSDK.traces.postTrace(traceRequest);
        return extractIdFromResponse(res);
    }

    private int extractIdFromResponse(String res) {
        return Integer.parseInt(res.substring("{\"id\":\"".length(), res.length() - 2));
    }
}
