package com.swygbro.trip.backend.domain.reservation.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationSearchCriteria {
    private boolean isPast = false;
    private int statusFilter = 0;
    private int offset = 0;
    private int pageSize = 5;
}
