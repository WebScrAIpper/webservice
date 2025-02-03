package com.polytech.webscraipper.sdk

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

@Service
class LangfuseSDK(
    promptsClient: Prompts.PromptsClient
) {
    @JvmField
    val prompts = Prompts(promptsClient)
}
