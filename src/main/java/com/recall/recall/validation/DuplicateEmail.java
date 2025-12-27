package com.recall.recall.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DuplicateEmailValidater.class)
public @interface DuplicateEmail {
    String message() default "Email is already exist";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
