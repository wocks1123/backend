package com.swygbro.trip.backend.domain.user.dto;

import com.swygbro.trip.backend.domain.guideProduct.dto.SimpleGuideProductDto;
import com.swygbro.trip.backend.domain.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

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

    private List<SimpleGuideProductDto> guideProducts;
    
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
