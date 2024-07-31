package com.swygbro.trip.backend.domain.alarm.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AlarmArgs {
    private Long fromUserId;
    private Long targetId;
}
