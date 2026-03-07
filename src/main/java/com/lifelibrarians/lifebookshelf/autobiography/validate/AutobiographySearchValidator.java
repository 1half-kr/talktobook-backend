package com.lifelibrarians.lifebookshelf.autobiography.validate;

import com.lifelibrarians.lifebookshelf.autobiography.dto.request.AutobiographySearchDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AutobiographySearchValidator implements
        ConstraintValidator<AutobiographySearchValidation, AutobiographySearchDto> {

    @Override
    public boolean isValid(AutobiographySearchDto value, ConstraintValidatorContext context) {
        // 자서전 제목 길이 제한 테스트
        if (value.getSearch() != null && !value.getSearch().isEmpty()
                && value.getSearch().length() > 64) {
            context.buildConstraintViolationWithTemplate(
                            "BIO005")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}