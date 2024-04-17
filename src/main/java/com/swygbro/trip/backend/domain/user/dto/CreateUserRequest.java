package com.swygbro.trip.backend.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateUserRequest {
    @NotBlank
    @Schema(description = "사용자 계정", example = "user01")
    private String account;

    @NotBlank
    @Email
    @Schema(description = "사용자 이메일", example = "user01@test.com")
    private String email;

    @NotBlank
    @Schema(description = "사용자 비밀번호", example = "password")
    private String password;
}
