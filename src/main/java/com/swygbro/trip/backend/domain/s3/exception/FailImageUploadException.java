package com.swygbro.trip.backend.domain.s3.exception;

import com.swygbro.trip.backend.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class FailImageUploadException extends BaseException {
    public FailImageUploadException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다.");
    }
}
