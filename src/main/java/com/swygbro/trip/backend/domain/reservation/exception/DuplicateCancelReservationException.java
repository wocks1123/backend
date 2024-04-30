package com.swygbro.trip.backend.domain.reservation.exception;

import com.swygbro.trip.backend.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class DuplicateCancelReservationException extends BaseException {
    public DuplicateCancelReservationException(String merchantUid) {
        super(HttpStatus.CONFLICT, "이미 취소 처리 된 예약입니다. 예약: (%s)".formatted(merchantUid));
    }
}
