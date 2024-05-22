package com.swygbro.trip.backend.domain.guideProduct.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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
    @Schema(description = "가이드 위치 이름", example = "한강 공원")
    private String locationName;
    @Schema(description = "가이드 시작 날짜", example = "2024-05-01")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate guideStart;
    @Schema(description = "가이드 종료 날짜", example = "2024-05-01")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate guideEnd;

    @QueryProjection
    public SearchGuideProductResponse(Long id, String title, String thumb, String locationName, ZonedDateTime guideStart, ZonedDateTime guideEnd) {
        this.id = id;
        this.title = title;
        this.thumb = thumb;
        this.locationName = locationName;
        this.guideStart = guideStart.toLocalDate();
        this.guideEnd = guideEnd.toLocalDate();
    }
}
