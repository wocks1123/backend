package com.swygbro.trip.backend.global.status;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Objects;

@Converter
public class ReservationStatusConverter implements AttributeConverter<ReservationStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(ReservationStatus attribute) {
        return attribute.getCode();
    }

    @Override
    public ReservationStatus convertToEntityAttribute(Integer dbData) {
        if (Objects.isNull(dbData)) {
            return null;
        }

        return ReservationStatus.of(dbData);
    }

}
