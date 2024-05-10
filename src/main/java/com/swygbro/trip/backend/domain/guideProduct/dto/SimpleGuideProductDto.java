package com.swygbro.trip.backend.domain.guideProduct.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;


/**
 * 유저 프로필에서 가이드 상품을 보여주기 위한 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SimpleGuideProductDto {
    @Schema(description = "가이드 상품 ID", example = "1")
    private Long id;
    @Schema(description = "가이드 상품 제목", example = "상품 이름")
    private String title;
    @Schema(description = "가이드 상품 설명", example = "설명")
    private String description;
    @Schema(description = "가이드 상품 섬네일", example = "https://S3저장소URL/저장위치/난수화된 이미지이름.이미지 타입")
    private String thumb;
    //    private Point location; // TODO 위치 정보 추가
    @Schema(description = "가이드 상품 시작 시간", example = "2024-05-05 00:00:00")
    private ZonedDateTime guideStart;
    @Schema(description = "가이드 상품 종료 시간", example = "2024-05-05 00:00:00")
    private ZonedDateTime guideEnd;
}
