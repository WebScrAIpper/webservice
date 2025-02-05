package com.polytech.webscraipper.sdk.responses

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class PromptResponse(
    @JvmField
    val id: String,
    @JvmField
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    val createdAt: LocalDateTime,
    @JvmField
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    val updatedAt: LocalDateTime,
    @JvmField
    val projectId: String,
    @JvmField
    val createdBy: String,
    @JvmField
    val type: String,
    @JvmField
    val prompt: String,
    @JvmField
    val name: String,
    @JvmField
    val version: Int,
    @JvmField
    val config: Any?,
    @JvmField
    val labels: List<String>,
    @JvmField
    val tags: List<String>,
    @JvmField
    val commitMessage: String?
)
