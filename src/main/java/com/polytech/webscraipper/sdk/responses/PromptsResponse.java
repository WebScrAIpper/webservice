package com.polytech.webscraipper.sdk.responses;

import java.util.List;

public class PromptsResponse {
  public final List<IndividualPrompt> data;

  public PromptsResponse(List<IndividualPrompt> data) {
    this.data = data;
  }

  public static class IndividualPrompt {
    public final String name;
    public final List<String> tags;
    public final String lastUpdatedAt;
    public final List<Integer> versions;
    public final List<String> labels;

    public IndividualPrompt(
        String name,
        List<String> tags,
        String lastUpdatedAt,
        List<Integer> versions,
        List<String> labels) {
      this.name = name;
      this.tags = tags;
      this.lastUpdatedAt = lastUpdatedAt;
      this.versions = versions;
      this.labels = labels;
    }
  }
}
