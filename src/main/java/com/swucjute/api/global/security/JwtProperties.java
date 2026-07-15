package com.swucjute.api.global.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** application.yml의 jwt.* 설정 매핑. */
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
    String secret, long accessTokenValiditySeconds, long refreshTokenValiditySeconds) {}
