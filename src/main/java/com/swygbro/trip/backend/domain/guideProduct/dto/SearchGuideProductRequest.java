package com.swygbro.trip.backend.domain.guideProduct.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class SearchGuideProductRequest {
    @Schema(description = "지역", example = "서울특별시")
    private String region;
    @Schema(description = "범위 시작 날짜", example = "2024-05-01")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate start;
    @Schema(description = "범위 종료 날짜", example = "2024-05-02")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate end;
}
