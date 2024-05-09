package com.swygbro.trip.backend.domain.guideProduct.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swygbro.trip.backend.domain.guideProduct.domain.GuideProduct;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchGuideProductResponse {
    @Schema(description = "상품 ID", example = "1")
    private Long id;
    @Schema(description = "상품 제목", example = "신나는 서울 투어")
    private String title;
    @Schema(description = "대표 이미지 url", example = "https://S3저장소URL/저장위치/난수화된 이미지이름.이미지 타입")
    private String thumb;
    @Schema(description = "가이드 시작 날짜/시간", example = "2024-05-01 12:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private ZonedDateTime guideStart;
    @Schema(description = "가이드 종료 날짜/시간", example = "2024-05-01 14:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private ZonedDateTime guideEnd;

    public static SearchGuideProductResponse fromEntity(GuideProduct product) {
        return SearchGuideProductResponse.builder()
                .id(product.getId())
                .title(product.getTitle())
                .thumb(product.getThumb())
                .guideStart(product.getGuideStart())
                .guideEnd(product.getGuideEnd())
                .build();
    }
}
