package com.swygbro.trip.backend.domain.admin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalTime;
import java.time.ZonedDateTime;

@Data
public class GuideProductDetailDto {

    private Long id;
    private String title;
    private String nickname;
    private String description;
    private String locationName;
    private Long price;
    @Schema(description = "가이드 상품 시작 일자", example = "2024-05-05", type = "string")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private ZonedDateTime guideStart;
    @Schema(description = "가이드 상품 종료 일자", example = "2024-06-06", type = "string")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private ZonedDateTime guideEnd;
    @Schema(description = "가이드 상품 시작 시간", example = "00:00:00")
    @JsonFormat(pattern = " HH:mm:ss")
    private LocalTime guideStartTime;
    @Schema(description = "가이드 상품 종료 시간", example = "00:00:00")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime guideEndTime;
    private int guideTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private ZonedDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private ZonedDateTime updatedAt;
}
