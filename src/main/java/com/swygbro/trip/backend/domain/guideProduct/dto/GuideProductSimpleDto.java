package com.swygbro.trip.backend.domain.guideProduct.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swygbro.trip.backend.domain.guideProduct.domain.GuideCategory;
import com.swygbro.trip.backend.domain.guideProduct.domain.GuideCategoryCode;
import com.swygbro.trip.backend.domain.guideProduct.domain.GuideProduct;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuideProductSimpleDto {
    @Schema(description = "유저 ID", example = "1")
    private Long userId;
    @Schema(description = "사용자 닉네임", example = "nickname01")
    private String nickname;
    @Schema(description = "상품 ID", example = "1")
    private Long id;
    @Schema(description = "상품 제목", example = "신나는 서울 투어")
    private String title;
    @Schema(description = "상품 설명", example = "서울 *** 여행 가이드 합니다.")
    private String description;
    @Schema(description = "가이드 비용", example = "10000")
    private Long price;
    @Schema(description = "가이드 위치 이름", example = "한강 공원")
    private String locationName;
    @Schema(description = "가이드 위치(위도)", example = "37")
    private double latitude;
    @Schema(description = "가이드 위치(경도)", example = "127")
    private double longitude;
    @Schema(description = "가이드 시작 날짜/시간", example = "2024-05-01 12:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private ZonedDateTime guideStart;
    @Schema(description = "가이드 종료 날짜/시간", example = "2024-05-01 14:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private ZonedDateTime guideEnd;
    @Schema(description = "가이드 소요 시간", example = "3")
    private int guideTime;
    @Schema(description = "상품 카테고리", example = "[\"DINING\", \"OUTDOOR\"]")
    private List<GuideCategoryCode> categories;
    @Schema(description = "대표 이미지 url", example = "https://S3저장소URL/저장위치/난수화된 이미지이름.이미지 타입")
    private String thumb;

    public static GuideProductSimpleDto fromEntity(GuideProduct product) {
        List<GuideCategoryCode> categories = product.getCategories().stream().map(GuideCategory::getCategoryCode).toList();

        return GuideProductSimpleDto.builder().id(product.getId())
                .userId(product.getUser().getId())
                .nickname(product.getUser().getNickname())
                .title(product.getTitle())
                .description(product.getDescription()).price(product.getPrice()).locationName(product.getLocationName())
                .longitude(product.getLocation().getX()).latitude(product.getLocation().getY())
                .guideStart(product.getGuideStart()).guideEnd(product.getGuideEnd())
                .guideTime(product.getGuideTime()).categories(categories)
                .thumb(product.getThumb())
                .build();
    }
}
