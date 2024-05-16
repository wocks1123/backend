package com.swygbro.trip.backend.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swygbro.trip.backend.domain.guideProduct.dto.SimpleGuideProductDto;
import com.swygbro.trip.backend.domain.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDto {
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

    @Schema(description = "사용자 생성일", example = "2024-05-05 00:00:00", type = "string")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private ZonedDateTime createdAt;

    private List<SimpleGuideProductDto> guideProducts;

    public void setReviewRating(Float reviewRating) {
        this.reviewRating = Float.parseFloat(String.format("%.1f", reviewRating));
    }

    public static UserProfileDto fromEntity(User user) {
        return UserProfileDto.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .name(user.getName())
                .profile(user.getProfile())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}
