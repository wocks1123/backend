package com.swygbro.trip.backend.domain.user.excepiton;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String account) {
        super("사용자를 찾을 수 없습니다. : %s".formatted(account));
    }
}
