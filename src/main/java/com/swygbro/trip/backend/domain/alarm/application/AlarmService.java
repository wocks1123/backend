package com.swygbro.trip.backend.domain.alarm.application;

import com.swygbro.trip.backend.domain.alarm.domain.AlarmRepository;
import com.swygbro.trip.backend.domain.alarm.domain.AlarmStorage;
import com.swygbro.trip.backend.domain.alarm.domain.EmitterRepository;
import com.swygbro.trip.backend.domain.alarm.dto.AlarmDto;
import com.swygbro.trip.backend.domain.alarm.exception.AlarmNotConnectException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmService {
    private final static Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private final EmitterRepository emitterRepository;
    private final AlarmRepository alarmRepository;
    private final AlarmStorage alarmStorage = new AlarmStorage();

    public SseEmitter connectAlarm(Long userId) {
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(userId, sseEmitter);

        sseEmitter.onCompletion(() -> emitterRepository.delete(userId));
        sseEmitter.onTimeout(() -> emitterRepository.delete(userId));

        try {
            sseEmitter.send(SseEmitter.event().id("id").name("Alarm").data("connect completed"));
        } catch (IOException e) {
            throw new AlarmNotConnectException(userId);
        }
        log.info("connect sse");
        sendPendingAlarm(userId, sseEmitter);

        return sseEmitter;
    }

    public void send(Long alarmId, Long receiveUserId, AlarmDto alarm) {
        emitterRepository.get(receiveUserId).ifPresentOrElse(sseEmitter -> {
            try {
                sseEmitter.send(SseEmitter.event().id(alarmId.toString()).name("Alarm").data(alarm.toString()));
                log.info("send alarm");
            } catch (IOException e) {
                alarmStorage.addAlarm(receiveUserId, alarm);
                throw new AlarmNotConnectException(receiveUserId);
            }
        }, () -> {
            alarmStorage.addAlarm(receiveUserId, alarm);
            log.info("No emitter founded");
        });
    }

    public void sendPendingAlarm(Long userId, SseEmitter emitter) {
        List<AlarmDto> alarms = alarmStorage.getAlarms(userId);
        if (alarms != null) {
            alarms.forEach(alarm -> {
                try {
                    emitter.send(SseEmitter.event().id(alarm.getId().toString()).name("Alarm").data(alarm.toString()));
                } catch (IOException e) {
                    alarmStorage.addAlarm(userId, alarm);
                    throw new AlarmNotConnectException(userId);
                }
            });
        }
    }

    public Page<AlarmDto> getAlarms(Long userId, Pageable pageable) {
        return alarmRepository.findAllByUserId(userId, pageable);
    }
}
