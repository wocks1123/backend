package com.swygbro.trip.backend.domain.user.dto;

import com.swygbro.trip.backend.domain.user.domain.Language;
import com.swygbro.trip.backend.global.dto.RequestDto;
import com.swygbro.trip.backend.global.validation.EnumClass;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UpdateUserRequest extends RequestDto {
    @NotBlank
    @Size(max = 100)
    @Schema(description = "자기소개", example = "안녕하세요!")
    private String profile;

    @NotBlank
    @Size(min = 2, max = 20)
    @Schema(description = "닉네임", example = "nickname_01")
    private String nickname;

    @Schema(description = "사용자의 언어 목록", example = "[\"ko\", \"en\"]")
    private List<@EnumClass(enumClass = Language.class) String> languages;
}
