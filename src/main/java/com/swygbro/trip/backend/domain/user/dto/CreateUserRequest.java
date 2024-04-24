package com.swygbro.trip.backend.domain.user.dto;

import com.swygbro.trip.backend.domain.user.domain.Gender;
import com.swygbro.trip.backend.global.validation.Phone;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest {
    @NotBlank
    @Size(max = 50)
    @Schema(description = "사용자 계정", example = "account01")
    private String account;

    @NotBlank
    @Size(max = 20)
    @Schema(description = "사용자 닉네임", example = "nickname01")
    private String nickname;

    @NotBlank
    @Size(max = 20)
    @Schema(description = "사용자 이름", example = "name01")
    private String name;

    @NotBlank
    @Size(max = 20)
    @Phone
    @Schema(description = "사용자 전화번호", example = "01012345678")
    private String phone;

    @NotBlank
    @Size(max = 20)
    @Schema(description = "사용자 국적", example = "KR")
    private String nationality;

    @NotBlank
    @Email
    @Schema(description = "사용자 이메일", example = "email@test.com")
    private String email;

    @Schema(description = "사용자 성별", example = "Male")
    private Gender gender;

    @NotBlank
    @Schema(description = "사용자 비밀번호", example = "password123")
    private String password;

    @NotBlank
    @Schema(description = "비밀번호 확인", example = "password123")
    private String passwordCheck;
}
