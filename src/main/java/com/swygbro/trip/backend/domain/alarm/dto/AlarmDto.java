package com.swygbro.trip.backend.domain.alarm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swygbro.trip.backend.domain.alarm.domain.Alarm;
import com.swygbro.trip.backend.domain.alarm.domain.AlarmArgs;
import com.swygbro.trip.backend.domain.alarm.domain.AlarmType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Getter
public class AlarmDto {
    @Schema(description = "알람 ID", example = "1")
    private Long id;
    @Schema(description = "알람 타입",
            example = "[\"NEW_RESERVATION_ALARM\"," +
                    "\"RESERVATION_CONFIRMED\"," +
                    "\"RESERVATION_REFUSED\"]," +
                    "\"RESERVATION_CANCEL\"]," +
                    "\"NEW_REVIEW\"]," +
                    "\"REVIEW_REQUEST\"]")
    private AlarmType alarmType;
    @Schema(description = "알람 정보",
            example = "\"FromUserId\" : \"알람을 보낸 유저 ID\", \"TargetId\" : \"타깃 ID\"")
    private AlarmArgs args;
    @Schema(description = "읽음 여부", example = "false")
    private boolean isRead;
    @Schema(description = "알람 수신 시간", example = "example = \"2024-05-01 12:00:00\"")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private ZonedDateTime createdAt;


    public static AlarmDto fromEntity(Alarm entity) {
        return AlarmDto
                .builder()
                .id(entity.getId())
                .alarmType(entity.getAlarmType())
                .args(entity.getArgs())
                .isRead(entity.getIsRead())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    @Override
    public String toString() {
        return "{AlarmId : " + id +
                ", FromUserId : " + args.getFromUserId() +
                ", TargetId : " + args.getTargetId() +
                ", AlarmType : " + alarmType.getAlarmText() +
                ", isRead : " + isRead +
                ", createdAt : " + createdAt.toLocalDateTime() +
                "}";
    }
}
