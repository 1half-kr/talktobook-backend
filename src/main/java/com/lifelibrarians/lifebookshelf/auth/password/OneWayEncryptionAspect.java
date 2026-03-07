package com.lifelibrarians.lifebookshelf.auth.password;

import com.lifelibrarians.lifebookshelf.exception.status.AuthExceptionStatus;
import com.lifelibrarians.lifebookshelf.auth.password.annotation.OneWayEncryption;
import com.lifelibrarians.lifebookshelf.auth.password.annotation.TargetMapping;
import com.lifelibrarians.lifebookshelf.utils.exception.UtilsExceptionStatus;
import java.lang.reflect.Field;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Aspect
@RequiredArgsConstructor
public class OneWayEncryptionAspect {

	private final PasswordEncoder passwordEncoder;

	/**
	 * @param oneWayEncryption 단방향 암호화 어노테이션
	 */
	@Pointcut("@annotation(oneWayEncryption)")
	public void oneWayEncryptionPointcut(OneWayEncryption oneWayEncryption) {
	}

	@Around(value = "oneWayEncryptionPointcut(oneWayEncryption)", argNames = "joinPoint,oneWayEncryption")
	public Object encode(ProceedingJoinPoint joinPoint, OneWayEncryption oneWayEncryption)
			throws Throwable {
		TargetMapping[] targetMappings = oneWayEncryption.value();

		Object[] methodArgs = joinPoint.getArgs();
		boolean isMatchedAtLeastOnce = false;
		for (TargetMapping targetMapping : targetMappings) {
			Class<?> targetClass = targetMapping.clazz();
			String[] targetFields = targetMapping.fields();

			for (Object arg : methodArgs) {
				if (arg.getClass().equals(targetClass)) {
					isMatchedAtLeastOnce = true;
					for (String targetField : targetFields) {
						String toEncode = getFieldFromTarget(arg, targetField, String.class);
						try {
							setFieldToTarget(arg, targetField,
									passwordEncoder.encode(toEncode));
						} catch (Exception e) {
							throw AuthExceptionStatus.PASSWORD_FORMAT_ERROR.toControllerException();
						}
					}
				}
			}
		}
		if (!isMatchedAtLeastOnce) {
			throw UtilsExceptionStatus.NON_MAPPED_TARGET.toControllerException();
		}
		return joinPoint.proceed(methodArgs);
	}

	/**
	 * 전달받은 target의 fieldName 에 해당하는 필드를 toConvert 타입으로 반환합니다.
	 *
	 * @param target    단방향 암호화가 필요한 필드를 가진 객체
	 * @param fieldName 단방향 암호화가 필요한 필드의 이름
	 * @param toConvert 단방향 암호화가 필요한 필드의 타입
	 * @param <T>       단방향 암호화가 필요한 필드의 타입
	 * @return 단방향 암호화가 필요한 필드를 난독화한 후, toConvert 타입으로 캐스팅하여 반환합니다.
	 */
	private <T> T getFieldFromTarget(Object target, String fieldName, Class<T> toConvert) {
		try {
			Field declaredField = target.getClass().getDeclaredField(fieldName);
			declaredField.setAccessible(true);
			Object targetField = declaredField.get(target);
			return toConvert.cast(targetField);
		} catch (IllegalAccessException | NoSuchFieldException | ClassCastException e) {
			throw UtilsExceptionStatus.NON_MAPPED_FIELD.toControllerException();
		}
	}

	/**
	 * 단방향 암호화된 필드를 객체에 적용합니다. setAccessible(true) 를 통해 private 필드에 접근한 후 set을 통해 단방향 암호화 합니다.
	 *
	 * @param target    단방향 암호화가 필요한 필드를 가진 객체
	 * @param fieldName 단방향 암호화가 필요한 필드의 이름
	 * @param value     단방향 암호화가 필요한 필드의 타입
	 */
	private void setFieldToTarget(Object target, String fieldName, Object value) {
		try {
			Field declaredField = target.getClass().getDeclaredField(fieldName);
			declaredField.setAccessible(true);
			declaredField.set(target, value);
		} catch (IllegalAccessException | NoSuchFieldException e) {
			throw UtilsExceptionStatus.NON_MAPPED_FIELD.toControllerException();
		}
	}
}
