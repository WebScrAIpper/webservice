package com.polytech.webscraipper.sdk

import feign.RequestInterceptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
open class FeignConfig(
    @Value("\${langfuse.api.username}")
    val username: String,
    @Value("\${langfuse.api.password}")
    val apiKey: String
)
{
    private val authHeader: String = "Basic " + Base64.getEncoder().encodeToString(("$username:$apiKey").toByteArray())


    @Bean
    open fun authInterceptor(): RequestInterceptor {
        return RequestInterceptor { template -> template.header("Authorization", authHeader) }
    }
}