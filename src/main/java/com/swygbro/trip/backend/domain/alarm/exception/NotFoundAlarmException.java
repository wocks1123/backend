package com.swygbro.trip.backend.domain.alarm.exception;

import com.swygbro.trip.backend.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class NotFoundAlarmException extends BaseException {
    public NotFoundAlarmException() {
        super(HttpStatus.NOT_FOUND, "해당 알람이 존재하지 않습니다.");
    }
}
