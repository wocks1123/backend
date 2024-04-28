package com.swygbro.trip.backend.domain.guideProduct.exception;

import com.swygbro.trip.backend.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class NotValidLocationException extends BaseException {
    public NotValidLocationException() {
        super(HttpStatus.BAD_REQUEST, "잘못된 위치입니다. 올바르게 입력해 주세요.");
    }
}
