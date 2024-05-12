package com.swygbro.trip.backend.domain.review.exception;

import com.swygbro.trip.backend.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidReviewRequestException extends BaseException {

    public InvalidReviewRequestException() {
        super(HttpStatus.BAD_REQUEST, "유효하지 않은 후기 작성 요청입니다.");
    }
}
