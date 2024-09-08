package com.swygbro.trip.backend.domain.alarm.exception;

import com.swygbro.trip.backend.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class AlarmNotConnectException extends BaseException {
    public AlarmNotConnectException(Long userId) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "해당 (%s) 유저와 연결에 실패했습니다".formatted(userId));
    }
}
