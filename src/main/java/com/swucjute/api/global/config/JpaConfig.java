package com.swucjute.api.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(
    basePackages = {
      "com.swucjute.api.domain.auth.repository",
      "com.swucjute.api.domain.home.repository",
      "com.swucjute.api.domain.member.repository",
      "com.swucjute.api.domain.platform.repository",
            "com.swucjute.api.domain.platformactivity.repository",
      "com.swucjute.api.domain.worship.repository"
    })
public class JpaConfig {}
