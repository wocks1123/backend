package com.swygbro.trip.backend.domain.alarm.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlarmType {
    NEW_RESERVATION_ALARM("새로운 예약 요청"),
    RESERVATION_CONFIRMED("예약 확정"),
    RESERVATION_REFUSED("예약 거절"),
    RESERVATION_CANCEL("예약 취소"),
    NEW_REVIEW("새로운 리뷰 작성됨"),
    REVIEW_REQUEST("새로운 리뷰 작성 요청");

    private final String alarmText;
}
