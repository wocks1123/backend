package com.swygbro.trip.backend.global.status;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ReservationStatus {
    RESERVED(0, "예약완료"),
    CANCELLED(1, "예약취소"),
    PENDING_CONFIRMATION(2, "예약확정대기"),
    SETTLED(3, "정산완료");

    private int code;
    private String status;
}
