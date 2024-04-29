package com.swygbro.trip.backend.global.status;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PayStatus {
    PENDING(0, "결제대기"),
    COMPLETE(1, "결제완료"),
    REFUNDED(2, "결제취소");

    private int code;
    private String status;
}
