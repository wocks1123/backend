package com.swygbro.trip.backend.domain.alarm.domain;

import com.swygbro.trip.backend.domain.user.domain.User;
import com.swygbro.trip.backend.global.entity.BaseEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Alarm extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "alarm_type", nullable = false)
    private AlarmType alarmType;

    @Type(JsonType.class)
    @Column(columnDefinition = "longtext", nullable = false)
    private AlarmArgs args;

    @Column(name = "is_read", columnDefinition = "TINYINT(1)", nullable = false)
    private Boolean isRead;

    public void setIsRead() {
        this.isRead = true;
    }

    public static Alarm of(User user, AlarmType alarmType, AlarmArgs args, boolean isRead) {
        return Alarm
                .builder()
                .user(user)
                .alarmType(alarmType)
                .args(args)
                .isRead(isRead)
                .build();
    }
}
