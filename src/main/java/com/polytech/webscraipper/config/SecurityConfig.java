package com.polytech.webscraipper.config;

import com.polytech.webscraipper.BaseLogger;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.web.filter.OncePerRequestFilter;

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
        .oauth2Login(oauth2 -> oauth2.successHandler(new CustomAuthenticationSuccessHandler()))
        .logout(
            logout ->
                logout
                    .logoutSuccessUrl("/")
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .deleteCookies("JSESSIONID"));

    http.addFilterBefore(new RedirectParameterFilter(), UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  public static class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private BaseLogger logger = new BaseLogger(SecurityConfig.class);

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request, HttpServletResponse response, Authentication authentication)
        throws IOException {

      // Retrieve redirect from cookie
      String redirect = null;
      if (request.getCookies() != null) {
        for (Cookie cookie : request.getCookies()) {
          if ("REDIRECT_URL".equals(cookie.getName())) {
            redirect = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
            break;
          }
        }
      }

      logger.debug("(Success) Redirecting to: " + redirect);
      if (redirect != null && UrlUtils.isAbsoluteUrl(redirect)) {
        response.sendRedirect(redirect);
      } else {
        response.sendRedirect("http://localhost:8088/realms/webscraipper-realm/account");
      }
    }
  }

  public static class RedirectParameterFilter extends OncePerRequestFilter {
    private BaseLogger logger = new BaseLogger(SecurityConfig.class);

    @Override
    protected void doFilterInternal(
        HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

      // Check if user is already authenticated
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      logger.debug(
          "(Internal Filter) User is connected: "
              + (authentication != null && authentication.isAuthenticated()));
      if (authentication != null && authentication.isAuthenticated()) {
        filterChain.doFilter(request, response);
        return;
      }
      String redirect = request.getParameter("redirect");
      logger.debug("(Internal Filter) Redirecting to: " + redirect);

      if (redirect != null) {
        String encodedRedirect = URLEncoder.encode(redirect, StandardCharsets.UTF_8);

        // Store the redirect URL in a cookie
        Cookie redirectCookie = new Cookie("REDIRECT_URL", encodedRedirect);
        redirectCookie.setPath("/");
        redirectCookie.setHttpOnly(true);
        response.addCookie(redirectCookie);

        String authorizationUrl = "/oauth2/authorization/keycloak";
        response.sendRedirect(authorizationUrl);
        return;
      }
      filterChain.doFilter(request, response);
    }
  }
}
