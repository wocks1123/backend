package com.swygbro.trip.backend.domain.review.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swygbro.trip.backend.domain.review.domain.Review;
import com.swygbro.trip.backend.domain.review.domain.ReviewImage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class DetailReviewDto {
    @Schema(description = "리뷰 고유 번호", example = "1")
    private Long reviewId;
    @Schema(description = "작성자 닉네임", example = "nickname01")
    private String reviewer;
    @Schema(description = "리뷰 내용", example = "정말 좋은 여행입니다.")
    private String content;
    @Schema(description = "별점", example = "5")
    private Integer rating;
    @Schema(description = "리뷰 생성일", example = "2024-05-05 00:00:00", type = "string")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private ZonedDateTime createdAt;
    @Schema(description = "리뷰 이미지", example = "이미지 url")
    private List<String> reviewImages;

    public static DetailReviewDto fromEntity(Review review) {
        return DetailReviewDto.builder()
                .reviewId(review.getId())
                .reviewer(review.getReviewer().getNickname())
                .reviewImages(review.getImages().stream().map(ReviewImage::getImageUrl).collect(Collectors.toList()))
                .content(review.getContent())
                .rating(review.getRating())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
