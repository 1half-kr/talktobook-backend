package com.lifelibrarians.lifebookshelf.common.validate;

import com.lifelibrarians.lifebookshelf.common.validate.dto.request.PaginationDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PaginationValidator implements ConstraintValidator<PaginationValidation, PaginationDto> {

    @Override
    public boolean isValid(PaginationDto dto, ConstraintValidatorContext context) {
        if (dto == null) return true; // null 허용 여부는 상황에 따라

        boolean isValid = true;

        if (dto.getPage() < 0) {
            context.buildConstraintViolationWithTemplate("C001")
                    .addConstraintViolation();
            return false;
        }

        if (dto.getSize() <= 0) {
            context.buildConstraintViolationWithTemplate("C002")
                    .addConstraintViolation();
            return false;
        }

        return isValid;
    }
}