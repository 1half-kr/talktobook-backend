package com.lifelibrarians.lifebookshelf.autobiography.validate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AutobiographySearchValidator.class)
public @interface AutobiographySearchValidation {
    String message() default "Invalid autobiography search parameters";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
