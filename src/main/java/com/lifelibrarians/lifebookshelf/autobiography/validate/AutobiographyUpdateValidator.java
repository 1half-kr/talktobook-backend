package com.lifelibrarians.lifebookshelf.autobiography.validate;

import com.lifelibrarians.lifebookshelf.autobiography.dto.request.AutobiographyUpdateRequestDto;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AutobiographyUpdateValidator implements
		ConstraintValidator<AutobiographyUpdateValidation, AutobiographyUpdateRequestDto> {

	@Override
	public boolean isValid(AutobiographyUpdateRequestDto value, ConstraintValidatorContext context) {
		// 자서전 제목 길이 제한 테스트
		if (value.getTitle() != null && !value.getTitle().isEmpty()
				&& value.getTitle().length() > 64) {
			context.buildConstraintViolationWithTemplate(
							"BIO005")
					.addConstraintViolation();
			return false;
		}

		// 자서전 내용 길이 제한 테스트
		if (value.getContent() != null && !value.getContent().isEmpty()
				&& value.getContent().length() > 30000) {
			context.buildConstraintViolationWithTemplate(
							"BIO006")
					.addConstraintViolation();
			return false;
		}
		return true;
	}
}
