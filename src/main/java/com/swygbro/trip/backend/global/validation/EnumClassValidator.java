package com.swygbro.trip.backend.global.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.ObjectUtils;


public class EnumClassValidator implements ConstraintValidator<EnumClass, String> {
    private EnumClass annotation;

    @Override
    public void initialize(EnumClass constraintAnnotation) {
        this.annotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (ObjectUtils.isEmpty(value)) return false;

        Object[] enumValues = this.annotation.enumClass().getEnumConstants();
        if (enumValues != null) {
            for (Object enumValue : enumValues) {
                if (value.equals(enumValue.toString())
                        || (this.annotation.ignoreCase() && value.equalsIgnoreCase(enumValue.toString()))) {
                    return true;
                }
            }
        }
        return false;
    }
}
