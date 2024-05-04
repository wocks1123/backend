package com.swygbro.trip.backend.domain.review.dto;

import com.swygbro.trip.backend.global.validation.Rating;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class CreateReviewRequest {
    @NotNull
    @Schema(description = "가이드 상품 ID", example = "1")
    private Long guideProductId;
    @Schema(description = "리뷰 내용", example = "정말 좋은 가이드입니다.")
    private String content;
    @NotNull
    @Rating
    @Schema(description = "별점", example = "5")
    private Integer rating;
}

