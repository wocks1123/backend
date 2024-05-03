package com.swygbro.trip.backend.domain.guideProduct.exception;

import com.swygbro.trip.backend.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class MismatchUserFromCreatorException extends BaseException {
    public MismatchUserFromCreatorException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
