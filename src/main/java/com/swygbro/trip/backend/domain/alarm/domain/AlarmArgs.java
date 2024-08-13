package com.swygbro.trip.backend.domain.alarm.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AlarmArgs<T> {
    private Long fromUserId;
    private T targetId;
}
