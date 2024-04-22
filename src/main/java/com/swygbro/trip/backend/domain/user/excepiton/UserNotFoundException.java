package com.swygbro.trip.backend.domain.user.excepiton;

import com.swygbro.trip.backend.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseException {
    public UserNotFoundException(String account) {
        super(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다. : %s".formatted(account));
    }
}
