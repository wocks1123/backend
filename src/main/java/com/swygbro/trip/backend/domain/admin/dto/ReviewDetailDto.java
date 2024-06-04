package com.swygbro.trip.backend.domain.admin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class ReviewDetailDto {
    private Long id;
    private Long reviewerId;
    private String reviewerNickname;
    private String reviewerEmail;
    private String profileImageUrl;
    private Long guideProductId;
    private Integer rating;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private ZonedDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private ZonedDateTime updatedAt;
}
