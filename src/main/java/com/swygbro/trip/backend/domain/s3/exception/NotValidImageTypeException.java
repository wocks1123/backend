package com.swygbro.trip.backend.domain.s3.exception;

import com.swygbro.trip.backend.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class NotValidImageTypeException extends BaseException {
    public NotValidImageTypeException() {
        super(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 이미지입니다. 다시 시도해주세요.");
    }
}
