package com.lifelibrarians.lifebookshelf.autobiography.validate;

import com.lifelibrarians.lifebookshelf.autobiography.dto.request.AutobiographyInitRequestDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AutobiographyInitValidator implements
        ConstraintValidator<AutobiographyInitValidation, AutobiographyInitRequestDto>  {

    @Override
    public boolean isValid(AutobiographyInitRequestDto value, ConstraintValidatorContext context) {
        // reason은 500자 이내
        if (value.getReason() != null && !value.getReason().isEmpty()
                && value.getReason().length() > 500) {
            context.buildConstraintViolationWithTemplate(
                            "BIO019")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
