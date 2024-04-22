package com.swygbro.trip.backend.domain.user.excepiton;

import com.swygbro.trip.backend.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class DuplicateUserAccountException extends BaseException {
    public DuplicateUserAccountException(String account) {
        super(HttpStatus.CONFLICT, "이미 존재하는 계정입니다. 계정: (%s)".formatted(account));
    }
}
