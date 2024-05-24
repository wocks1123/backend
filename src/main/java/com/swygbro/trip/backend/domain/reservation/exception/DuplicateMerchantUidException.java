package com.swygbro.trip.backend.domain.reservation.exception;

import com.swygbro.trip.backend.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class DuplicateMerchantUidException extends BaseException {
    public DuplicateMerchantUidException(String impUid) {
        super(HttpStatus.CONFLICT, "이미 존재하는 merchant_uid 입니다. : (%s)".formatted(impUid));
    }
}
