package com.swygbro.trip.backend.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swygbro.trip.backend.domain.guideProduct.domain.GuideProduct;
import com.swygbro.trip.backend.domain.user.domain.Language;
import com.swygbro.trip.backend.domain.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDto {
    @Schema(description = "사용자 ID", example = "1")
    private Long id;

    @Schema(description = "사용자 이메일", example = "user01@test.com")
    private String email;

    @Schema(description = "사용자 닉네임", example = "nickname01")
    private String nickname;

    @Schema(description = "사용자 이름", example = "name01")
    private String name;

    @Schema(description = "사용자 프로필", example = "안녕하세요!")
    private String profile;

    @Schema(description = "사용자 프로필 이미지 URL", example = "/images/profile.jpg")
    private String profileImageUrl;

    @Schema(description = "리뷰 수", example = "10")
    private Integer reviewCount;

    @Schema(description = "리뷰 평균 별점", example = "4.6")
    private Float reviewRating;

    @Schema(description = "사용자가 사용하는 언어 목록", example = "[\"ko\", \"en\"]")
    private List<Language> languages;

    @Schema(description = "사용자 생성일", example = "2024-05-05 00:00:00", type = "string")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private ZonedDateTime createdAt;

    @Schema(description = "사용자가 등록한 가이드 상품 목록")
    private List<SimpleGuideProductDto> guideProducts;

    public static UserProfileDto fromEntity(User user) {
        return UserProfileDto.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .name(user.getName())
                .profile(user.getProfile())
                .createdAt(user.getCreatedAt())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }

    @Getter
    @Builder
    public static class SimpleGuideProductDto {
        @Schema(description = "가이드 상품 ID", example = "1")
        private Long id;
        @Schema(description = "가이드 상품 제목", example = "상품 이름")
        private String title;
        @Schema(description = "가이드 상품 설명", example = "설명")
        private String description;
        @Schema(description = "가이드 상품 섬네일", example = "https://S3저장소URL/저장위치/난수화된 이미지이름.이미지 타입")
        private String thumb;
        @Schema(description = "가이드 상품 위치", example = "서울")
        private String locationName;
        @Schema(description = "가이드 상품 시작 일자", example = "2024-05-05", type = "string")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private ZonedDateTime guideStart;
        @Schema(description = "가이드 상품 종료 일자", example = "2024-06-06", type = "string")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private ZonedDateTime guideEnd;
        @Schema(description = "가이드 상품 시작 시간", example = "00:00:00")
        @JsonFormat(pattern = "HH:mm:ss")
        private LocalTime guideStartTime;
        @Schema(description = "가이드 상품 종료 시간", example = "00:00:00")
        @JsonFormat(pattern = "HH:mm:ss")
        private LocalTime guideEndTime;
        private int guideTime;
        @Schema(description = "가이드 상품 생성일", example = "2024-05-05 00:00:00")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private ZonedDateTime createdAt;

        public static SimpleGuideProductDto fromEntity(GuideProduct guideProduct) {
            return SimpleGuideProductDto.builder()
                    .id(guideProduct.getId())
                    .title(guideProduct.getTitle())
                    .description(guideProduct.getDescription())
                    .thumb(guideProduct.getThumb())
                    .locationName(guideProduct.getLocationName())
                    .guideStart(guideProduct.getGuideStart())
                    .guideEnd(guideProduct.getGuideEnd())
                    .guideStartTime(guideProduct.getGuideStartTime())
                    .guideEndTime(guideProduct.getGuideEndTime())
                    .guideTime(guideProduct.getGuideTime())
                    .createdAt(guideProduct.getCreatedAt())
                    .build();
        }
    }
}

