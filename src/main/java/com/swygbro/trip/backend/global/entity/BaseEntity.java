package com.swygbro.trip.backend.global.entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.ZonedDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    private ZonedDateTime createdAt;
    private String createdBy;

    private ZonedDateTime updatedAt;
    private String updatedBy;

    private ZonedDateTime deletedAt;
    private String deletedBy;

}
