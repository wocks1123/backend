package com.swygbro.trip.backend.domain.guideProduct.exception;

import com.swygbro.trip.backend.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class MismatchUserFromCreatorException extends BaseException {
    public MismatchUserFromCreatorException() {
        super(HttpStatus.UNAUTHORIZED, "가이드 상품을 수정할 권한이 없습니다.");
    }
}
