package com.swygbro.trip.backend.domain.guideProduct.exception;

import com.swygbro.trip.backend.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class GuideProductNotInRangeException extends BaseException {
    public GuideProductNotInRangeException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
