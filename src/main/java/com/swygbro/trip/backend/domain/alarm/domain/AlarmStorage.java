package com.swygbro.trip.backend.domain.alarm.domain;

import com.swygbro.trip.backend.domain.alarm.dto.AlarmDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlarmStorage {
    private final Map<Long, List<AlarmDto>> alarmMap = new HashMap<>();

    public synchronized void addAlarm(Long userId, AlarmDto alarm) {
        alarmMap.computeIfAbsent(userId, k -> new ArrayList<>()).add(alarm);
    }

    public synchronized List<AlarmDto> getAlarms(Long userId) {
        return alarmMap.remove(userId);
    }
}
