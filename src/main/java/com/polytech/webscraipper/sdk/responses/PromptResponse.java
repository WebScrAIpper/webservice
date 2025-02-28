package com.polytech.webscraipper.sdk.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

public class PromptResponse {
  public final String id;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
  public final LocalDateTime createdAt;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
  public final LocalDateTime updatedAt;

  public final String projectId;
  public final String createdBy;
  public final String type;
  public String prompt;
  public final String name;
  public final int version;
  public final Object config;
  public final List<String> labels;
  public final List<String> tags;
  public final String commitMessage;

  // Jackson requires a no-args constructor to deserialize objects.
  // Since all fields are final, we must initialize them with default values.
  public PromptResponse() {
    this.id = null;
    this.createdAt = null;
    this.updatedAt = null;
    this.projectId = null;
    this.createdBy = null;
    this.type = null;
    this.prompt = null;
    this.name = null;
    this.version = 0;
    this.config = null;
    this.labels = null;
    this.tags = null;
    this.commitMessage = null;
  }

  // Using @JsonCreator to tell Jackson how to construct this object when deserializing.
  // Each @JsonProperty ensures that the JSON key is correctly mapped to the constructor parameter.
  @JsonCreator
  public PromptResponse(
      @JsonProperty("id") String id,
      @JsonProperty("createdAt") LocalDateTime createdAt,
      @JsonProperty("updatedAt") LocalDateTime updatedAt,
      @JsonProperty("projectId") String projectId,
      @JsonProperty("createdBy") String createdBy,
      @JsonProperty("type") String type,
      @JsonProperty("prompt") String prompt,
      @JsonProperty("name") String name,
      @JsonProperty("version") int version,
      @JsonProperty("config") Object config,
      @JsonProperty("labels") List<String> labels,
      @JsonProperty("tags") List<String> tags,
      @JsonProperty("commitMessage") String commitMessage) {
    this.id = id;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.projectId = projectId;
    this.createdBy = createdBy;
    this.type = type;
    this.prompt = prompt;
    this.name = name;
    this.version = version;
    this.config = config;
    this.labels = labels;
    this.tags = tags;
    this.commitMessage = commitMessage;
  }
}
