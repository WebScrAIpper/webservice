package com.polytech.webscraipper.sdk

import com.polytech.webscraipper.sdk.responses.PromptResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

class Prompts(private val promptsClient: PromptsClient) {

    fun getAll(): List<PromptResponse> = promptsClient.getAllPrompts()

    @JvmOverloads
    fun getById(promptId: String, version: Int? = null, label: String? = null): PromptResponse {
        return promptsClient.getPromptById(promptId, version, label)
    }

    @FeignClient(
        name = "langfuse-sdk",
        url = "https://cloud.langfuse.com/api/public/v2/prompts",
        configuration = [FeignConfig::class]
    )
    interface PromptsClient {

        @GetMapping("/")
        fun getAllPrompts(): List<PromptResponse>

        @GetMapping("/{promptId}")
        fun getPromptById(
            @PathVariable("promptId") promptId: String,
            @RequestParam("version", required = false) version: Int? = null,
            @RequestParam("label", required = false) label: String? = null
        ): PromptResponse
    }
}
