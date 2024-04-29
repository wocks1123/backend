package com.swygbro.trip.backend.domain.guideProduct.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swygbro.trip.backend.domain.guideProduct.domain.GuideCategoryCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuideProductRequest {
    private String account;
    @NotBlank
    @Schema(description = "상품 제목", example = "신나는 서울 투어")
    private String title;
    @NotBlank
    @Schema(description = "상품 설명", example = "서울 *** 여행 가이드 합니다.")
    private String description;
    private Long price;
    private double longitude;
    private double latitude;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private ZonedDateTime guideStart;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private ZonedDateTime guideEnd;
    private List<GuideCategoryCode> categories;

}
