package com.polytech.webscraipper.sdk.responses


data class ResponseWrapper<Result>(
    val data: Result,
    // we dont care about metadata
)
