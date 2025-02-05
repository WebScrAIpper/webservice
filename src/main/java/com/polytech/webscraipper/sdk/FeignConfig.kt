package com.polytech.webscraipper.sdk

import feign.Client
import feign.RequestInterceptor
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*
import java.util.concurrent.TimeUnit

@Configuration
open class FeignConfig(
    @Value("\${langfuse.api.username}")
    val username: String,
    @Value("\${langfuse.api.password}")
    val apiKey: String,
) {
    private val authHeader: String = "Basic " + Base64.getEncoder().encodeToString(("$username:$apiKey").toByteArray())

    @Bean
    open fun authInterceptor(): RequestInterceptor = RequestInterceptor { template -> template.header("Authorization", authHeader) }

    @Bean
    open fun feignClient(): Client {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .followRedirects(true)
            .build()
        return feign.okhttp.OkHttpClient(okHttpClient)
    }
}
