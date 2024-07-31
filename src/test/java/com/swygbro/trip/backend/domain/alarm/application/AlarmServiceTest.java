package com.swygbro.trip.backend.domain.alarm.application;

import com.swygbro.trip.backend.domain.alarm.domain.AlarmArgs;
import com.swygbro.trip.backend.domain.alarm.domain.AlarmType;
import com.swygbro.trip.backend.domain.alarm.dto.AlarmDto;
import com.swygbro.trip.backend.domain.user.TestUserFactory;
import com.swygbro.trip.backend.domain.user.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("비즈니스 로직 - 알람")
@ExtendWith(MockitoExtension.class)
public class AlarmServiceTest {

    @InjectMocks
    AlarmService alarmService;

    private User user;

    @BeforeEach
    void setup() {
        user = TestUserFactory.createTestUser();
    }

    @DisplayName("알림 구독")
    @Test
    void newSubscribe() {
        // given
        Long userId = 1L;

        // when, then
        Assertions.assertDoesNotThrow(() -> alarmService.connectAlarm(userId));
    }

    @DisplayName("알림 전송")
    @Test
    void send() {
        // given
        Long userId = 1L;
        Long productId = 1L;
        Long guideId = 2L;
        Long alarmId = 1L;

        AlarmDto alarmDto = new AlarmDto(alarmId,
                AlarmType.NEW_RESERVATION_ALARM,
                new AlarmArgs(userId, productId),
                false);

        alarmService.connectAlarm(guideId);

        // when, then
        Assertions.assertDoesNotThrow(() -> alarmService.send(alarmId, guideId, alarmDto));

    }
}
