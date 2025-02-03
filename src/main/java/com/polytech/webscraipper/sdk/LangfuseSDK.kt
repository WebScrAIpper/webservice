package com.polytech.webscraipper.sdk

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

class LangfuseSDK(
    username: String,
    apiKey: String
) {
    private val authHeader: String = "Basic " + Base64.getEncoder().encodeToString(("$username:$apiKey").toByteArray())

    val prompts = Prompts()
}