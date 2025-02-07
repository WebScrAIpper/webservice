package com.polytech.webscraipper.sdk

import io.netty.util.internal.UnstableApi
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.*

class Traces(private val tracesClient: TracesClient) {

    fun postTrace(traceData: Map<String, Any>): String = tracesClient.postTrace(traceData)

    @UnstableApi
    fun postTrace(vararg entries: Pair<String, Any>): String = postTrace(entries.toMap())

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
