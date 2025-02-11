package com.polytech.webscraipper.sdk.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
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

  public PromptResponse(
      String id,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      String projectId,
      String createdBy,
      String type,
      String prompt,
      String name,
      int version,
      Object config,
      List<String> labels,
      List<String> tags,
      String commitMessage) {
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
