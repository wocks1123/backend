package com.swygbro.trip.backend.global.status;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PayStatus {
    PENDING("결제대기"),
    COMPLETE("결제완료"),
    FAILED("결제취소");

    private String status;
}
