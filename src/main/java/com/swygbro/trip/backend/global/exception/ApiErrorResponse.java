package com.swygbro.trip.backend.global.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

@Getter
public class ApiErrorResponse {
    @Schema(title = "상태코드", example = "000", implementation = String.class)
    private final HttpStatusCode status;
    @Schema(title = "에러 메시지", example = "에러가 발생했습니다.")
    private final String message;

    public ApiErrorResponse(HttpStatusCode httpStatus, String message) {
        this.status = httpStatus;
        this.message = message;
    }

    public ApiErrorResponse(BaseException ex) {
        status = ex.getHttpStatus();
        message = ex.getMessage();
    }

    public static ResponseEntity<ApiErrorResponse> toResponseEntity(HttpStatusCode httpStatusCode, String message) {
        return ResponseEntity.status(httpStatusCode).body(new ApiErrorResponse(httpStatusCode, message));
    }

    public static ResponseEntity<ApiErrorResponse> toResponseEntity(BaseException ex) {
        return ResponseEntity.status(ex.getHttpStatus()).body(new ApiErrorResponse(ex));
    }

}
