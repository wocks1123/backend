package com.swygbro.trip.backend.domain.guideProduct.exception;

import com.swygbro.trip.backend.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class GuideProductNotFoundException extends BaseException {
    public GuideProductNotFoundException(int productId) {
        super(HttpStatus.NOT_FOUND, "해당 (%s) 가이드 상품을 찾을 수 없습니다.".formatted(productId));
    }
}
