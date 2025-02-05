package com.polytech.webscraipper.sdk

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.*

class Traces(private val tracesClient: TracesClient) {

    fun postTrace(traceData: Map<String, Any>): String = tracesClient.postTrace(traceData)

    fun postTrace(vararg entries: Pair<String, Any>): String = postTrace(entries.toMap())

    fun postGenericAILog(
        promptName: String,
        promptVersion: Int,
        response: String,
        sourceUrl: String,
        sessionId: String,
    ) {
        val traceRequest = mapOf(
            "name" to "LLM Request",
            "promptName" to promptName,
            "promptVersion" to promptVersion,
            "response" to response,
            "sourceUrl" to sourceUrl,
            "sessionId" to sessionId,
        )
        postTrace(traceRequest)
    }

    @FeignClient(
        name = "langfuse-sdk-traces",
        url = "https://cloud.langfuse.com/api/public/traces",
        configuration = [FeignConfig::class],
    )
    interface TracesClient {
        @PostMapping("/")
        fun postTrace(@RequestBody traceRequest: Map<String, Any>): String
    }
}
