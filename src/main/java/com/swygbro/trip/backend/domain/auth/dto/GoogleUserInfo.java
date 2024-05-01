package com.swygbro.trip.backend.domain.auth.dto;

import lombok.Getter;

@Getter
public class GoogleUserInfo {
    private String id;
    private String email;
    private boolean verified_email;
    private String picture;
}
