package com.swygbro.trip.backend.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Value("${version}")
    private String version;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().components(new Components())
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("Matthew")
                .description("Matthew에서 사용하는 API를 확인할 수 있습니다.")
                .version(version);
    }

}
