package com.swygbro.trip.backend.global.status;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReservationStatus {
    RESERVED("예약완료"),
    CANCELLED("예약취소"),
    PENDING_CONFIRMATION("예약확정대기");
    private String status;
}
