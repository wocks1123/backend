package com.swygbro.trip.backend.domain.guideProduct.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swygbro.trip.backend.domain.guideProduct.domain.GuideCategoryCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModifyGuideProductRequest {
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
    @Schema(description = "가이드 위치 이름", example = "한강 공원")
    private String locationName;
    @NotNull
    @Schema(description = "가이드 위치(위도)", example = "37")
    private double latitude;
    @NotNull
    @Schema(description = "가이드 위치(경도)", example = "127")
    private double longitude;
    @Schema(description = "가이드 시작 날짜", example = "2024-05-01", type = "string")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate guideStart;
    @NotNull
    @Schema(description = "가이드 종료 날짜", example = "2024-05-01", type = "string")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate guideEnd;
    @NotNull
    @Schema(description = "가이드 시작 시간", example = "12:00:00", type = "string")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss", timezone = "Asia/Seoul")
    private LocalTime guideStartTime;
    @NotNull
    @Schema(description = "가이드 종료 시간", example = "20:00:00", type = "string")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss", timezone = "Asia/Seoul")
    private LocalTime guideEndTime;
    @NotNull
    @Schema(description = "가이드 소요 시간", example = "3")
    private int guideTime;
    @NotNull
    @Schema(description = "상품 카테고리", example = "[\"DINING\", \"OUTDOOR\"]")
    private List<GuideCategoryCode> categories;
    @Schema(description = "대표 이미지 url", example = "https://S3저장소URL/저장위치/난수화된 이미지이름.이미지 타입")
    private String thumb;
    @Schema(description = "상품 이미지 url", example = "[\"https://S3저장소URL/저장위치/난수화된 이미지이름.이미지 타입\", \"...\"]")
    private List<String> images = new ArrayList<>();

}
