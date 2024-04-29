package com.swygbro.trip.backend.global.status;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Objects;

@Converter
public class ReservationStatusConverter implements AttributeConverter<ReservationStatus, String> {
    @Override
    public String convertToDatabaseColumn(ReservationStatus attribute) {
        if (Objects.isNull(attribute)){
            throw new NullPointerException("ReservationStatus is null");
        }

        return attribute.toString();
    }

    @Override
    public ReservationStatus convertToEntityAttribute(String dbData) {
        return ReservationStatus.valueOf(dbData);
    }
}
