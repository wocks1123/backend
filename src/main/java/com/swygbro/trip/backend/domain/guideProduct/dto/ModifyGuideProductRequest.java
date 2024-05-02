package com.swygbro.trip.backend.domain.guideProduct.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swygbro.trip.backend.domain.guideProduct.domain.GuideCategoryCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModifyGuideProductRequest {
    @Schema(description = "사용자 계정", example = "추후에 토큰에서 가져와서 사용")
    private String email;
    @NotBlank
    @Schema(description = "상품 제목", example = "신나는 서울 투어")
    private String title;
    @NotBlank
    @Schema(description = "상품 설명", example = "서울 *** 여행 가이드 합니다.")
    private String description;
    @NotNull
    @Schema(description = "가이드 비용", example = "10000")
    private Long price;
    @NotNull
    @Schema(description = "가이드 위치(위도)", example = "37")
    private double longitude;
    @NotNull
    @Schema(description = "가이드 위치(경도)", example = "127")
    private double latitude;
    @NotNull
    @Schema(description = "가이드 시작 날짜/시간", example = "2024-05-01 12:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private ZonedDateTime guideStart;
    @NotNull
    @Schema(description = "가이드 종료 날짜/시간", example = "2024-05-01 14:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private ZonedDateTime guideEnd;
    @NotNull
    @Schema(description = "상품 카테고리", example = "[\"C1\", \"C2\"]")
    private List<GuideCategoryCode> categories;
    @Schema(description = "대표 이미지 url", example = "https://S3저장소URL/저장위치/난수화된 이미지이름.이미지 타입")
    private String thumb;
    @Schema(description = "상품 이미지 url", example = "[\"https://S3저장소URL/저장위치/난수화된 이미지이름.이미지 타입\", \"...\"]")
    private List<String> images = new ArrayList<>();

}
