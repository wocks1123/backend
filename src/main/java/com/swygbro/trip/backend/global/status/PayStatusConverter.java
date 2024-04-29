package com.swygbro.trip.backend.global.status;


import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Objects;

@Converter
public class PayStatusConverter implements AttributeConverter<PayStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(PayStatus attribute) {
        return attribute.getCode();
    }

    @Override
    public PayStatus convertToEntityAttribute(Integer dbData) {
        if (Objects.isNull(dbData)) {
            return null;
        }

        return PayStatus.of(dbData);
    }
}