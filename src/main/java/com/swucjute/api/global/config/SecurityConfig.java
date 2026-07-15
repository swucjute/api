package com.swucjute.api.global.config;

import com.swucjute.api.domain.auth.oauth.KakaoOAuth2UserService;
import com.swucjute.api.domain.auth.oauth.OAuth2LoginFailureHandler;
import com.swucjute.api.domain.auth.oauth.OAuth2LoginSuccessHandler;
import com.swucjute.api.global.security.JwtAuthenticationEntryPoint;
import com.swucjute.api.global.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  private final KakaoOAuth2UserService kakaoOAuth2UserService;
  private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
  private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .formLogin(form -> form.disable())
        .httpBasic(basic -> basic.disable())
        // OAuth2 인가 요청/콜백 처리에만 세션이 필요하므로 IF_REQUIRED. API 인증은 JWT로 무상태 처리.
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(
                        "/api/v1/auth/**",
                        "/oauth2/**",
                        "/login/oauth2/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/actuator/health")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/worships/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/home/**", "/api/v1/home")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/platforms", "/api/v1/platforms/*")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .oauth2Login(
            oauth ->
                oauth
                    .userInfoEndpoint(userInfo -> userInfo.userService(kakaoOAuth2UserService))
                    .successHandler(oAuth2LoginSuccessHandler)
                    .failureHandler(oAuth2LoginFailureHandler))
        .exceptionHandling(
            handling -> handling.authenticationEntryPoint(jwtAuthenticationEntryPoint))
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
