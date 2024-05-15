package com.swygbro.trip.backend.domain.review.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
public class ReviewDetailDto {
    @Schema(description = "리뷰 고유 번호", example = "1")
    private Long reviewId;
    @Schema(description = "작성자 닉네임", example = "nickname01")
    private String reviewer;
    @Schema(description = "가이드 상품 고유 번호", example = "1")
    private Long guideProductId;
    @Schema(description = "리뷰 내용", example = "정말 좋은 여행입니다.")
    private String content;
    @Schema(description = "별점", example = "5")
    private Integer rating;
    @Schema(description = "이미지 링크들", example = "['http://s3bucket.com/000', ...]", type = "string")
    private List<String> images;
    @Schema(description = "리뷰 생성일", example = "2024-05-05 00:00:00", type = "string")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private ZonedDateTime createdAt;
    @Schema(description = "리뷰 수정일 | 수정내역 없을 경우 null", example = "2024-05-05 00:00:00", type = "string")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private ZonedDateTime updatedAt;
}
