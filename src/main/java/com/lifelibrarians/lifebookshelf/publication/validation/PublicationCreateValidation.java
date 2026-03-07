package com.lifelibrarians.lifebookshelf.publication.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PublicationCreateValidator.class)
public @interface PublicationCreateValidation {

	String message() default "출판 생성 요청 데이터 형식이 올바르지 않습니다.";

	Class<?>[] groups() default {};

	Class<?>[] payload() default {};
}
