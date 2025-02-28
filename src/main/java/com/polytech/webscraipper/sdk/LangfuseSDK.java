package com.polytech.webscraipper.sdk;

import org.springframework.stereotype.Service;

@Service
public class LangfuseSDK {
  public final Prompts prompts;
  public final Traces traces;

  public LangfuseSDK(Prompts.PromptsClient promptsClient, Traces.TracesClient tracesClient) {
    this.prompts = new Prompts(promptsClient);
    this.traces = new Traces(tracesClient);
  }
}
