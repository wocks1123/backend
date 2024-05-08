package com.swygbro.trip.backend.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
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
                new Tag().name("Reservation-Client").description("""
                                                
                        ### 개요
                                
                        - 여행객의 예약 관련 기능을 제공합니다.
                                
                        ### 주요 기능
                                
                        - 예약 정보 조회
                        - 예약 리스트 조회
                        - 결제 검증
                        - 예약 정보 저장
                        - 결제 정보 저장
                        - 예약 취소

                        """),
                new Tag().name("Reservation-Guide").description("""
                                                
                        ### 개요
                                
                        - 가이드의 예약 관련 기능을 제공합니다.
                                
                        ### 주요 기능
                                
                        - 예약 리스트 조회
                        - 예약 확정
                        - 예약 취소

                        """),
                new Tag().name("Guide Products").description("""
                                                
                        ### 개요
                                
                        - 가이드 상품의 기본 CRUD를 제공합니다.
                                
                        ### 주요 기능
                                
                        - 가이드 상품 생성
                        - 가이드 상품 조회
                        - 가이드 상품 수정
                        - 가이드 상품 삭제

                        """),
                new Tag().name("Search Guide Products").description("""
                                                
                        ### 개요
                                
                        - 여러 조건으로 가이드 상품을 조회합니다.
                                
                        ### 주요 기능
                                
                        - 현재 위치 기준 30km 범위 내 가이드 상품들 조회
                        - 지역과 날짜로 가이드 상품들 검색
                        - 추가적인 필터 기능

                        """)
        );
    }
}
