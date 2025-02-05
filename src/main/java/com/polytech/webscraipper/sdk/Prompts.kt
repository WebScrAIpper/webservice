package com.polytech.webscraipper.sdk

import com.polytech.webscraipper.sdk.responses.PromptResponse
import com.polytech.webscraipper.sdk.responses.PromptsResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

class Prompts(private val promptsClient: PromptsClient) {

    @JvmOverloads
    fun getPromptVariables(promptName: String, version: Int? = null, label: String? = null): List<String> {
        val prompt = getByName(promptName, version, label)
        return extractVariablesFromText(prompt.prompt)
    }

    @JvmOverloads
    fun getCustomizedPrompt(promptName: String, version: Int? = null, label: String? = null, variables: Map<String, String>): String {
        val prompt = getByName(promptName, version, label)
        return replacesVariablesInText(prompt.prompt, variables)
    }

    fun getAll(): List<PromptsResponse.IndividualPrompt> = promptsClient.getAllPrompts().data

    @JvmOverloads
    fun getByName(promptName: String, version: Int? = null, label: String? = null): PromptResponse = promptsClient.getPromptByName(promptName, version, label)

    @FeignClient(
        name = "langfuse-sdk-prompts",
        url = "https://cloud.langfuse.com/api/public/v2/prompts",
        configuration = [FeignConfig::class],
    )
    interface PromptsClient {

        @GetMapping("/")
        fun getAllPrompts(): PromptsResponse

        @GetMapping("/{promptId}")
        fun getPromptByName(
            @PathVariable("promptId") promptId: String,
            @RequestParam("version", required = false) version: Int? = null,
            @RequestParam("label", required = false) label: String? = null,
        ): PromptResponse
    }

    fun extractVariablesFromText(text: String): List<String> {
        val regex = Regex("\\{\\{([^}]*)}}")
        return regex.findAll(text).map { it.groupValues[1] }.toList()
    }

    fun replacesVariablesInText(text: String, variables: Map<String, String>): String {
        var newText = text

        val reelVariables = extractVariablesFromText(text)

        if (!variables.keys.containsAll(reelVariables)) {
            throw IllegalArgumentException("Not all variables have a value")
        }

        variables.forEach { (key, value) -> newText = newText.replace("{{$key}}", value) }
        return newText
    }
}
