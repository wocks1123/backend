package com.swygbro.trip.backend.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swygbro.trip.backend.domain.user.domain.Gender;
import com.swygbro.trip.backend.domain.user.domain.Nationality;
import com.swygbro.trip.backend.domain.user.domain.SignUpType;
import lombok.Data;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Data
public class UserDetailDto {
    private Long id;
    private String email;
    private String nickname;
    private String name;
    private String phone;
    private String location;
    private Nationality nationality;
    private LocalDate birthdate;
    private String profileImageUrl;
    private Gender gender;
    private SignUpType signUpType;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private ZonedDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private ZonedDateTime updatedAt;
}
