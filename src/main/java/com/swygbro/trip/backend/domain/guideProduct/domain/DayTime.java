package com.swygbro.trip.backend.domain.guideProduct.domain;

import lombok.Getter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Getter
public enum DayTime {
    ALL(0, 23),
    DAWN(0, 6),
    MORNING(7, 11),
    LUNCH(12, 17),
    EVENING(18, 23);

    private final String start;
    private final String end;

    DayTime(int start, int end) {
        String startTime = LocalTime.of(start, 0, 0).format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String endTime = LocalTime.of(end, 59, 59).format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        this.start = startTime;
        this.end = endTime;
    }

}
