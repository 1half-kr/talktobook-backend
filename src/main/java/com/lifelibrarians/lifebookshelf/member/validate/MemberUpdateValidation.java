package com.lifelibrarians.lifebookshelf.member.validate;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MemberUpdateValidator.class)
public @interface MemberUpdateValidation {

	String message() default "회원 정보 수정 요청 데이터 형식이 올바르지 않습니다.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
