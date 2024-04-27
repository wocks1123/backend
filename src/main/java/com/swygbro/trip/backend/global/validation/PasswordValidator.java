package com.swygbro.trip.backend.global.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, String> {
    private static final String PASSWORD_PATTERN = "^(?=.*[a-zA-Z])(?=.*[~!@#$%^&*()_+=\\-<>/\\\\\\[\\]\\{\\}`|])(?=.*[0-9]).{8,}$";

    @Override
    public void initialize(Password constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext context) {
        return s.matches(PASSWORD_PATTERN);
    }
}
