package com.swygbro.trip.backend.domain.user.dto;

import com.swygbro.trip.backend.global.jwt.dto.TokenDto;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class LoginResponseDto {
    UserInfoDto user;
    TokenDto token;
}
