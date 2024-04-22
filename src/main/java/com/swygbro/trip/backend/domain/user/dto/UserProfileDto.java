package com.swygbro.trip.backend.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class UserProfileDto {
    @Schema(description = "사용자 계정", example = "user01")
    private String account;
    @Schema(description = "사용자 이메일", example = "user01@test.com")
    private String email;
}
