package com.swygbro.trip.backend.domain.user.dto;

import com.swygbro.trip.backend.global.dto.RequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateUserRequest extends RequestDto {
    @NotBlank
    @Size(max = 100)
    @Schema(description = "자기소개", example = "안녕하세요!")
    private String profile;
}
