package com.swygbro.trip.backend.domain.alarm.dto;

import com.swygbro.trip.backend.domain.alarm.domain.Alarm;
import com.swygbro.trip.backend.domain.alarm.domain.AlarmArgs;
import com.swygbro.trip.backend.domain.alarm.domain.AlarmType;
import com.swygbro.trip.backend.global.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Getter
public class AlarmDto extends BaseEntity {
    private Long id;
    private AlarmType alarmType;
    private AlarmArgs args;
    private boolean isRead;

    public static AlarmDto fromEntity(Alarm entity) {
        return AlarmDto
                .builder()
                .id(entity.getId())
                .alarmType(entity.getAlarmType())
                .args(entity.getArgs())
                .isRead(entity.getIsRead())
                .build();
    }

    @Override
    public String toString() {
        return "{AlarmId : " + id +
                ", FromUserId : " + args.getFromUserId() +
                ", TargetProductId : " + args.getTargetId() +
                ", AlarmType : " + alarmType.getAlarmText() +
                ", isRead : " + isRead +
                "}";
    }
}
