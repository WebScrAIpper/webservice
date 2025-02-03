package com.polytech.webscraipper.sdk.responses

data class PromptResponse(
    val type: String,
    val prompt: List<Prompt>,
    val name: String,
    val version: Int,
    val config: Any?,
    val labels: List<String>,
    val tags: List<String>,
    val commitMessage: String
) {
    data class Prompt(
        val role: String,
        val content: String
    )
}
//    {
//        "type": "chat",
//        "prompt": [
//        {
//            "role": "…",
//            "content": "…"
//        }
//        ],
//        "name": "…",
//        "version": 1,
//        "config": null,
//        "labels": [
//        "…"
//        ],
//        "tags": [
//        "…"
//        ],
//        "commitMessage": "…"
//    }