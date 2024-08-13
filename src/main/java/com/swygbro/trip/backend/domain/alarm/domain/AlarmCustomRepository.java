package com.swygbro.trip.backend.domain.alarm.domain;

import com.swygbro.trip.backend.domain.alarm.dto.AlarmDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AlarmCustomRepository {
    Page<AlarmDto> findAllByUserId(Long userId, Integer isRead, Pageable pageable);
}
