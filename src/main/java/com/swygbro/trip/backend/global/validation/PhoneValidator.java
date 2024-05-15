package com.swygbro.trip.backend.global.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneValidator implements ConstraintValidator<Phone, String> {
    private static final String PHONE_PATTERN = "^[+]?[0-9]{10,15}$";

    @Override
    public void initialize(Phone constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            // 핸드폰은 선택사항이기 때문에 null 허용
            return true;
        }
        return value.matches(PHONE_PATTERN);
    }
}
