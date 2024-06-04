package com.swygbro.trip.backend.global.exception;

import com.siot.IamportRestClient.exception.IamportResponseException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class ExternalApiException {
    public static ResponseEntity<ApiErrorResponse> of(Exception ex) {
        if (ex instanceof IamportResponseException subEx) {
            return ApiErrorResponse.toResponseEntity(HttpStatus.valueOf(subEx.getHttpStatusCode()), subEx.getMessage());
        }

        return ApiErrorResponse.toResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
    }
}
