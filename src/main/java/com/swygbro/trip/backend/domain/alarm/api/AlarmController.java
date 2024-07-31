package com.swygbro.trip.backend.domain.alarm.api;

import com.swygbro.trip.backend.domain.alarm.application.AlarmService;
import com.swygbro.trip.backend.domain.alarm.dto.AlarmDto;
import com.swygbro.trip.backend.domain.user.domain.User;
import com.swygbro.trip.backend.global.jwt.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    @GetMapping("/alarm/subscribe")
    public SseEmitter subscribe(@CurrentUser User user) {
        return alarmService.connectAlarm(user.getId());
    }

    @GetMapping("/alarm")
    public Page<AlarmDto> getAlarms(@CurrentUser User user,
                                    @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return alarmService.getAlarms(user.getId(), pageable);
    }
}
