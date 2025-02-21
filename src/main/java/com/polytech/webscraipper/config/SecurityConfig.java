package com.polytech.webscraipper.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfig class configures security settings for the application, enabling security filters
 * and setting up OAuth2 login and logout behavior.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  /**
   * Configures the security filter chain for handling HTTP requests, OAuth2 login, and logout.
   *
   * @param http HttpSecurity object to define web-based security at the HTTP level
   * @return SecurityFilterChain for filtering and securing HTTP requests
   * @throws Exception in case of an error during configuration
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
        // Configures OAuth2 login settings
        .oauth2Login(
            oauth2 ->
                oauth2
                    .loginPage("/oauth2/authorization/keycloak")
                    .defaultSuccessUrl(
                        "http://localhost:8088/realms/webscraipper-realm/account", true))
        // **(NOT WORKING)** Configures logout settings
        .logout(
            logout ->
                logout
                    .logoutSuccessUrl("/") // Redirects to the root URL on successful logout
                    .invalidateHttpSession(true) // Invalidates session to clear session data
                    .clearAuthentication(true) // Clears authentication details
                    .deleteCookies("JSESSIONID") // Deletes the session cookie
            );

    return http.build();
  }
}
