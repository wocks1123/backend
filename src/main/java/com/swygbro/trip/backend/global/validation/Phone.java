package com.swygbro.trip.backend.global.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PhoneValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Phone {
    String message() default "핸드폰 번호 양식이 올바르지 않습니다.('-' 제외)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
