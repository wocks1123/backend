package com.swygbro.trip.backend.domain.admin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
public class ReviewInfoDto {
    private Long id;
    private Long reviewerId;
    private String reviewer;
    private Long guideProductId;
    private Long reservationId;
    private Integer rating;
    private String content;
    private List<String> images;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private ZonedDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private ZonedDateTime updatedAt;
}
