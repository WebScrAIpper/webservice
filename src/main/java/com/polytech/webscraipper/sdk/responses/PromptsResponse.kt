package com.polytech.webscraipper.sdk.responses

data class PromptsResponse(
    @JvmField
    val data: List<IndividualPrompt>,
    // we dont care about metadata
) {
    data class IndividualPrompt(
        @JvmField
        val name: String,
        @JvmField
        val tags: List<String>,
        @JvmField
        val lastUpdatedAt: String,
        @JvmField
        val versions: List<Int>,
        @JvmField
        val labels: List<String>,
    )
}
