package com.polytech.webscraipper.sdk

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.cloud.openfeign.RefreshableUrl
import org.springframework.web.bind.annotation.*

class Traces(private val tracesClient: TracesClient) {

    fun postTrace(traceRequest: Map<String, Any>): String {
        return tracesClient.postTrace(traceRequest)
    }

    fun postTrace(vararg traceRequest: Pair<String, Any>): String {
        return postTrace(traceRequest.toMap())
    }

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
            "sessionId" to sessionId
        )
        postTrace(traceRequest)
    }

    @FeignClient(
        name = "langfuse-sdk-traces",
        url = "https://cloud.langfuse.com/api/public/traces",
        configuration = [FeignConfig::class]
    )
    interface TracesClient {
        @PostMapping("/")
        fun postTrace(@RequestBody traceRequest: Map<String, Any>): String
    }
}