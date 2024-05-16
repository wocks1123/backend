package com.swygbro.trip.backend.domain.review.exception;

import com.swygbro.trip.backend.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ReviewNotFoundException extends BaseException {

    public ReviewNotFoundException() {
        super(HttpStatus.NOT_FOUND, "해당 리뷰를 찾을 수 없습니다.");
    }
}