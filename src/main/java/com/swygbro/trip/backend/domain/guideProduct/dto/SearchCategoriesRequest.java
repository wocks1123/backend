package com.swygbro.trip.backend.domain.guideProduct.dto;

import com.swygbro.trip.backend.domain.guideProduct.domain.GuideCategoryCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchCategoriesRequest {
    @Schema(description = "사용자 위치(위도)", example = "34.567")
    private Double latitude;
    @Schema(description = "사용자 위치(경도)", example = "127.235")
    private Double longitude;
    @Schema(description = "상품 카테고리", example = "DINING")
    private GuideCategoryCode category;
}
