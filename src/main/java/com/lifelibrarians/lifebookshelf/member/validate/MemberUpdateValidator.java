package com.lifelibrarians.lifebookshelf.member.validate;

import com.lifelibrarians.lifebookshelf.member.dto.request.MemberUpdateRequestDto;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MemberUpdateValidator implements
		ConstraintValidator<MemberUpdateValidation, MemberUpdateRequestDto> {

	@Override
	public boolean isValid(MemberUpdateRequestDto value,
			ConstraintValidatorContext context) {
		return true;
	}
}
