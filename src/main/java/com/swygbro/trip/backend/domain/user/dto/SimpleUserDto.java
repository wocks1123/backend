package com.swygbro.trip.backend.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swygbro.trip.backend.domain.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;


@Data
@Builder
public class SimpleUserDto {
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

    @Schema(description = "사용자 생성일", example = "2024-05-05 00:00:00", type = "string")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private ZonedDateTime createdAt;

    public static SimpleUserDto fromEntity(User user) {
        return SimpleUserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .name(user.getName())
                .profile(user.getProfile())
                .createdAt(user.getCreatedAt())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}
