package com.swygbro.trip.backend.domain.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationSearchCriteria {
    @Schema(description = "시점 필터 {0: 과거, 1: 미래, null: all}", example = "0")
    private int timeFilter;

    @Schema(description = "상태 필터 {0: 예약 확정 대기, 1: 예약 확정, 2: 예약 취소", example = "0")
    private int statusFilter = 0;

    @Schema(description = "오프셋", example = "0")
    private int offset = 0;

    @Schema(description = "페이지 크기", example = "5")
    private int pageSize = 5;
}
