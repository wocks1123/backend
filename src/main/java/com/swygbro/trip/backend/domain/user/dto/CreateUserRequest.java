package com.swygbro.trip.backend.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swygbro.trip.backend.domain.user.domain.Gender;
import com.swygbro.trip.backend.domain.user.domain.Nationality;
import com.swygbro.trip.backend.global.dto.RequestDto;
import com.swygbro.trip.backend.global.validation.Phone;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateUserRequest extends RequestDto {

    @NotBlank
    @Email
    @Schema(description = "사용자 이메일", example = "user01@email.com")
    private String email;

    @NotBlank
    @Size(min = 2, max = 20)
    @Schema(description = "사용자 닉네임", example = "nickname01")
    private String nickname;

    @NotBlank
    @Size(max = 20)
    @Schema(description = "사용자 이름", example = "name01")
    private String name;

    @Size(max = 20)
    @Phone // TODO 핸드폰번호 검증양식 추가
    @Schema(description = "사용자 전화번호", example = "+01012345678")
    private String phone;

    @Size(max = 100)
    @Schema(description = "사용자 위치", example = "Seoul")
    private String location;

    @Schema(description = "사용자 국적", example = "KOR")
    private Nationality nationality;

    @NotBlank
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "사용자 생년월일", example = "1990-01-01")
    private String birthdate;

    @Schema(description = "사용자 성별", example = "Male")
    private Gender gender;

    @NotBlank
    @Size(min = 8, max = 20)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "비밀번호는 영문과 숫자를 포함하여 8자 이상이어야 합니다.")
    @Schema(description = "사용자 비밀번호", example = "password123")
    private String password;

    @NotBlank
    @Schema(description = "비밀번호 확인", example = "password123")
    private String passwordCheck;
}


