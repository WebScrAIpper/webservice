package com.polytech.webscraipper.sdk

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.*

class Traces(private val tracesClient: TracesClient) {

    fun postTrace(traceData: Map<String, Any>): String = tracesClient.postTrace(traceData)

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
