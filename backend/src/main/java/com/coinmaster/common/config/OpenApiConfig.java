package com.coinmaster.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI coinMasterOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("CoinMaster API")
                        .version("v1")
                        .description("Authentication, market, trading, portfolio and Gemini insights API"))
                .components(new Components().addSecuritySchemes(
                        "bearerAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .description("Redis-backed session token returned by the login endpoint")
                ));
    }
}
