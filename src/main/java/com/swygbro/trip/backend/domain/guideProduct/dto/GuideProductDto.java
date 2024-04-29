package com.swygbro.trip.backend.domain.guideProduct.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swygbro.trip.backend.domain.guideProduct.domain.GuideCategory;
import com.swygbro.trip.backend.domain.guideProduct.domain.GuideCategoryCode;
import com.swygbro.trip.backend.domain.guideProduct.domain.GuideProduct;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GuideProductDto {
    private Long id;
    private String account;
    @Schema(description = "상품 제목", example = "신나는 서울 투어")
    private String title;
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
    @Schema(description = "상품 이미지")
    private List<GuideImageDto> images;

    public static GuideProductDto fromEntity(GuideProduct product) {
        List<GuideImageDto> images = product.getImages().stream().map(GuideImageDto::fromEntity).toList();
        List<GuideCategoryCode> categories = product.getCategories().stream().map(GuideCategory::getCategoryCode).toList();

        return new GuideProductDto(
                product.getId(),
                product.getUser().getAccount(),
                product.getTitle(),
                product.getDescription(),
                product.getPrice(),
                product.getLocation().getY(),
                product.getLocation().getX(),
                product.getGuideStart(),
                product.getGuideEnd(),
                categories,
                images
        );
    }
}
