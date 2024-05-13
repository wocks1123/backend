package com.swygbro.trip.backend.domain.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationSearchCriteria {
    @Schema(description = "과거/미래 여부", example = "false")
    private boolean isPast;

    @Schema(description = "상태 필터 {0: 예약 확정 대기, 1: 예약 확정, 2: 예약 취소", example = "0")
    private int statusFilter = 0;

    @Schema(description = "오프셋", example = "0")
    private int offset = 0;

    @Schema(description = "페이지 크기", example = "5")
    private int pageSize = 5;
}
