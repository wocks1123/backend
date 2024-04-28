package com.swygbro.trip.backend.domain.guideProduct.dto;

import com.swygbro.trip.backend.domain.guideProduct.domain.GuideCategoryCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class CreateGuideProductRequest {
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
    private ZonedDateTime guideStart;
    private ZonedDateTime guideEnd;
    private List<GuideCategoryCode> categories;

}
