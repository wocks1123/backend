package com.swygbro.trip.backend.domain.reservation.exception;

import com.swygbro.trip.backend.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class DuplicateImpUidException extends BaseException {
    public DuplicateImpUidException(String impUid) {
        super(HttpStatus.CONFLICT, "이미 존재하는 imp_uid 입니다. : (%s)".formatted(impUid));
    }
}
