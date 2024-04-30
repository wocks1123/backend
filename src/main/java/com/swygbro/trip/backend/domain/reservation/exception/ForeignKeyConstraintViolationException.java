package com.swygbro.trip.backend.domain.reservation.exception;

import com.swygbro.trip.backend.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ForeignKeyConstraintViolationException extends BaseException {
    public ForeignKeyConstraintViolationException(String tableName) {
        super(HttpStatus.CONFLICT, "존재하지 않는 외래키입니다. : %s".formatted(tableName));
    }
}
