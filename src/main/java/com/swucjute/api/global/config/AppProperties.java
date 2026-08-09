package com.swucjute.api.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** application.yml의 app.* 설정 매핑. */
@ConfigurationProperties(prefix = "app")
public record AppProperties(String frontendBaseUrl) {}
