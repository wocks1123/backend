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
    @Schema(description = "상품 ID", example = "1")
    private Long id;
    private String email;
    @Schema(description = "상품 제목", example = "신나는 서울 투어")
    private String title;
    @Schema(description = "상품 설명", example = "서울 *** 여행 가이드 합니다.")
    private String description;
    @Schema(description = "가이드 비용", example = "10000")
    private Long price;
    @Schema(description = "가이드 위치(위도)", example = "37")
    private double longitude;
    @Schema(description = "가이드 위치(경도)", example = "127")
    private double latitude;
    @Schema(description = "가이드 시작 날짜/시간", example = "2024-05-01 12:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private ZonedDateTime guideStart;
    @Schema(description = "가이드 종료 날짜/시간", example = "2024-05-01 14:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private ZonedDateTime guideEnd;
    @Schema(description = "상품 카테고리", example = "[\"C1\", \"C2\"]")
    private List<GuideCategoryCode> categories;
    @Schema(description = "상품 이미지 url", example = "[{\"url\": \"https://S3저장소URL/저장위치/난수화된 이미지이름.이미지 타입\"}]")
    private List<GuideImageDto> images;

    public static GuideProductDto fromEntity(GuideProduct product) {
        List<GuideImageDto> images = product.getImages().stream().map(GuideImageDto::fromEntity).toList();
        List<GuideCategoryCode> categories = product.getCategories().stream().map(GuideCategory::getCategoryCode).toList();

        return new GuideProductDto(
                product.getId(),
                product.getUser().getEmail(),
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
