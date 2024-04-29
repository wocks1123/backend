package com.swygbro.trip.backend.global.status;


import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Objects;

@Converter
public class PayStatusConverter implements AttributeConverter<PayStatus, String> {
    @Override
    public String convertToDatabaseColumn(PayStatus attribute) {
        if (Objects.isNull(attribute)){
            throw new NullPointerException("PayStatus is null");
        }

        return attribute.toString();
    }

    @Override
    public PayStatus convertToEntityAttribute(String dbData) {
        return PayStatus.valueOf(dbData);
    }
}