package com.swucjute.api.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI openAPI() {
    String jwtSchemeName = "Bearer Token";
    SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);
    SecurityScheme securityScheme =
        new SecurityScheme()
            .name(jwtSchemeName)
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT");

    return new OpenAPI()
        .info(new Info().title("주뜨 청년부 앱 API").description("주뜨 청년부 앱 REST API 명세").version("v1"))
        .addSecurityItem(securityRequirement)
        .components(new Components().addSecuritySchemes(jwtSchemeName, securityScheme));
  }
}
