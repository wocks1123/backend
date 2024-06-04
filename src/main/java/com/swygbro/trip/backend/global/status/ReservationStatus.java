package com.swygbro.trip.backend.global.status;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ReservationStatus {
    PENDING_CONFIRMATION(0, "예약확정대기"),
    RESERVED(1, "예약완료"),
    CANCELLED(2, "예약취소"),
    SETTLED(3, "정산완료");

    private int code;
    private String status;

    public static ReservationStatus of(int code) {
        for (ReservationStatus reservationStatus : ReservationStatus.values()) {
            if (reservationStatus.getCode() == code) {
                return reservationStatus;
            }
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }
}
