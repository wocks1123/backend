package com.swygbro.trip.backend.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${version}")
    private String version;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components().addSecuritySchemes("access-token",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .info(apiInfo())
                .tags(tagList());
    }

    private Info apiInfo() {
        return new Info()
                .title("Matthew")
                .description("Matthew에서 사용하는 API를 확인할 수 있습니다.")
                .version(version);
    }

    private List<Tag> tagList() {
        return List.of(
                new Tag().name("Reservation-Client").description("여행객의 예약 관련 기능을 제공합니다."),
                new Tag().name("Reservation-Guide").description("가이드의 예약 관련 기능을 제공합니다.")
        );
    }
}
