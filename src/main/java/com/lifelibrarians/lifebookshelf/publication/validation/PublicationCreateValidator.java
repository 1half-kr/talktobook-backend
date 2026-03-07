package com.lifelibrarians.lifebookshelf.publication.validation;

import com.lifelibrarians.lifebookshelf.publication.dto.request.PublicationCreateRequestDto;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PublicationCreateValidator implements
		ConstraintValidator<PublicationCreateValidation, PublicationCreateRequestDto> {

	@Override
	public boolean isValid(PublicationCreateRequestDto value,
			ConstraintValidatorContext context) {

		if (value.getTitle() == null || value.getTitle().isEmpty()) {
			context.buildConstraintViolationWithTemplate(
							"PUB003")
					.addConstraintViolation();
			return false;
		}
		if (value.getTitle().length() < 2 || value.getTitle().length() > 64) {
			context.buildConstraintViolationWithTemplate(
							"PUB003")
					.addConstraintViolation();
			return false;
		}

		return true;
	}
}
