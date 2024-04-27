package com.swygbro.trip.backend.domain.guideProduct.dto;

import com.swygbro.trip.backend.domain.guideProduct.domain.GuideProduct;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GuideProductDto {
    @Schema(description = "상품 제목", example = "신나는 서울 투어")
    private String title;
    @Schema(description = "상품 설명", example = "서울 *** 여행 가이드 합니다.")
    private String description;
    @Schema(description = "상품 이미지")
    private List<GuideImageDto> images;

    public static GuideProductDto fromEntity(GuideProduct product) {
        List<GuideImageDto> images = product.getImages().stream().map(GuideImageDto::fromEntity).toList();

        return new GuideProductDto(
                product.getTitle(),
                product.getDescription(),
                images
        );
    }
}
