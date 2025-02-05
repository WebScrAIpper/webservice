package com.polytech.webscraipper.sdk

import org.springframework.stereotype.Service
@Service
class LangfuseSDK(
    promptsClient: Prompts.PromptsClient,
    tracesClient: Traces.TracesClient,
) {
    @JvmField
    val prompts = Prompts(promptsClient)

    @JvmField
    val traces = Traces(tracesClient)
}
