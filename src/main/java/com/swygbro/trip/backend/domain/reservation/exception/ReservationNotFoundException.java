package com.swygbro.trip.backend.domain.reservation.exception;

import com.swygbro.trip.backend.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ReservationNotFoundException extends BaseException {
    public ReservationNotFoundException(String merchantUid) {
        super(HttpStatus.NOT_FOUND, "예약 정보를 찾을 수 없습니다. : %s".formatted(merchantUid));
    }
}
