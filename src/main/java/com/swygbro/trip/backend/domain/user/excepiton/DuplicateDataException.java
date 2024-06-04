package com.swygbro.trip.backend.domain.user.excepiton;

import com.swygbro.trip.backend.global.exception.BaseException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DuplicateDataException extends BaseException {
    public DuplicateDataException(String data) {
        super(HttpStatus.CONFLICT, "이미 존재하는 데이터입니다.: (%s)".formatted(data));
    }

}
