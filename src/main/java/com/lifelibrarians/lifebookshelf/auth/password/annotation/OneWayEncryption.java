package com.lifelibrarians.lifebookshelf.auth.password.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 단방향 암호화가 필요한 객체를 전달받는 메소드에 적용하는 어노테이션
 * <p>
 * 단방향 암호화가 필요한 Class 와 필드를 명시합니다. Ex) PasswordMember 클래스의 password 필드를 단방향 암호화할 경우
 *
 * @DataEncode({
 * @TargetMapping(clazz = PasswordMember.class, fields = {TestDto.Fields.password}) )}
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OneWayEncryption {

	TargetMapping[] value();
}
