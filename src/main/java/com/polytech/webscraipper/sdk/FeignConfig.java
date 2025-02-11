package com.polytech.webscraipper.sdk;

import feign.Client;
import feign.RequestInterceptor;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

  private final String authHeader;

  public FeignConfig(
      @Value("${langfuse.api.username}") String username,
      @Value("${langfuse.api.password}") String apiKey) {
    this.authHeader =
        "Basic "
            + Base64.getEncoder()
                .encodeToString((username + ":" + apiKey).getBytes(StandardCharsets.UTF_8));
  }

  @Bean
  public RequestInterceptor authInterceptor() {
    return template -> template.header("Authorization", authHeader);
  }

  @Bean
  public Client feignClient() {
    OkHttpClient okHttpClient =
        new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .followRedirects(true)
            .build();
    return new feign.okhttp.OkHttpClient(okHttpClient);
  }
}
