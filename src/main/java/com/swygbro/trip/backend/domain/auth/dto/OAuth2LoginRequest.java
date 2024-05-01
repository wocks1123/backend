package com.swygbro.trip.backend.domain.auth.dto;

import lombok.Getter;

@Getter
public class OAuth2LoginRequest {
    private String code;
}
