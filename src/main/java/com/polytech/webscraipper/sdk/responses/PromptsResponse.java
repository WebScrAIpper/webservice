package com.polytech.webscraipper.sdk.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PromptsResponse {

  @JsonProperty("data")
  public List<IndividualPrompt> data;

  public PromptsResponse() {} // Default constructor

  public PromptsResponse(List<IndividualPrompt> data) {
    this.data = data;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class IndividualPrompt {

    @JsonProperty("name")
    public String name;

    @JsonProperty("tags")
    public List<String> tags;

    @JsonProperty("lastUpdatedAt")
    public String lastUpdatedAt;

    @JsonProperty("versions")
    public List<Integer> versions;

    @JsonProperty("labels")
    public List<String> labels;

    public IndividualPrompt() {} // Default constructor

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
