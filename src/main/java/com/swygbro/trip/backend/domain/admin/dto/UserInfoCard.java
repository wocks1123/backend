package com.swygbro.trip.backend.domain.admin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swygbro.trip.backend.domain.user.domain.Gender;
import com.swygbro.trip.backend.domain.user.domain.Language;
import com.swygbro.trip.backend.domain.user.domain.Nationality;
import com.swygbro.trip.backend.domain.user.domain.SignUpType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
public class UserInfoCard {

    private Long id;
    private String email;
    private String nickname;
    private String profile;
    private String profileImageUrl;
    private String phone;
    private String location;
    private Nationality nationality;
    private LocalDate birthdate;
    private Gender gender;
    private SignUpType signUpType;
    private List<Language> languages;
    // 등록한 여행 수
    private long guideProductCount;
    // 등록한 여행의 예약 수
    private long guideProductReservationCount;
    // 등록한 여행의 평점
    private float guideProductReviewRatingAvg;
    // 등록한 여행의 리뷰 수
    private long guideProductReviewCount;
    // 내가 예약한 여행 수
    private long myReservationCount;
    // 내가 준 평정
    private float myReviewRatingAvg;
    // 내가 쓴 리뷰 수
    private long myReviewCount;
    @Schema(description = "사용자 생성일", example = "2024-05-05 00:00:00", type = "string")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private ZonedDateTime createdAt;
    @Schema(description = "사용자 수정일", example = "2024-05-05 00:00:00", type = "string")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private ZonedDateTime updatedAt;
}
