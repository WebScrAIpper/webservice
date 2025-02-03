package com.polytech.webscraipper.sdk

import org.springframework.cloud.openfeign.FeignClient

@FeignClient(name = "Langfuse SDK", url = "https://cloud.langfuse.com/api/public/prompts")
class Prompts(val authHeader: String) {


    fun getPrompt()
}