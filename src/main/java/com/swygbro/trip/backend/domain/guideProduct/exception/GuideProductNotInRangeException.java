package com.swygbro.trip.backend.domain.guideProduct.exception;

import com.swygbro.trip.backend.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class GuideProductNotInRangeException extends BaseException {
    public GuideProductNotInRangeException() {
        super(HttpStatus.NOT_FOUND, "주변에 가이드 상품이 존재하지 않습니다.");
    }
}
