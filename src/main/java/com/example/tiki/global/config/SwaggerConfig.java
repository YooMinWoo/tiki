package com.example.tiki.global.config;

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
        String keyName = "access";  // JWT 헤더 키 이름

        SecurityRequirement securityRequirement = new SecurityRequirement().addList(keyName);

        Components components = new Components()
                .addSecuritySchemes(keyName, new SecurityScheme()
                        .name(keyName)
                        .type(SecurityScheme.Type.APIKEY) // HTTP 타입이 아님
                        .in(SecurityScheme.In.HEADER)     // 헤더에 포함
                );

        return new OpenAPI()
                .info(apiInfo())
                .addSecurityItem(securityRequirement)
                .components(components);
    }

    private Info apiInfo() {
        return new Info()
                .title("Guide Me API")
                .description("Guide Me API입니다.")
                .version("1.0.0");
    }
}
