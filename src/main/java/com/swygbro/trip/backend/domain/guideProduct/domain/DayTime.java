package com.swygbro.trip.backend.domain.guideProduct.domain;

import lombok.Getter;

@Getter
public enum DayTime {
    ALL(0, 23),
    DAWN(0, 6),
    MORNING(7, 11),
    LUNCH(12, 17),
    EVENING(18, 23);

    private final int start;
    private final int end;

    DayTime(int start, int end) {
        this.start = start;
        this.end = end;
    }

}
