package com.lifelibrarians.lifebookshelf.exception.status;

import com.lifelibrarians.lifebookshelf.exception.ControllerException;
import com.lifelibrarians.lifebookshelf.exception.DomainException;
import com.lifelibrarians.lifebookshelf.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public enum CommonExceptionStatus implements ExceptionStatus {
    PAGE_OUT_OF_BOUNDS(400, "C001", "page는 0 혹은 양수 값이어야 합니다."),
    SIZE_OUT_OF_BOUNDS(400, "C002", "size는 0 이상의 양수 값이어야 합니다."),
    YEAR_OUT_OF_BOUNDS(400, "C003", "year는 2000 이상의 값이어야 합니다."),
    MONTH_OUT_OF_BOUNDS(400, "C004", "month는 1과 12 사이의 값이어야 합니다."),
    INVALID_YEAR(400, "COO3", "year는 2000 이상의 값이어야 합니다."),
    INVALID_MONTH(400, "COO4", "month는 1과 12 사이의 값이어야 합니다.");

    private final int statusCode;
    private final String code;
    private final String message;

    @Override
    public ErrorReason getErrorReason() {
        return ErrorReason.builder()
                .statusCode(statusCode)
                .code(code)
                .message(message)
                .build();
    }

    @Override
    public ControllerException toControllerException() {
        return new ControllerException(this);
    }

    @Override
    public ServiceException toServiceException() {
        return new ServiceException(this);
    }

    @Override
    public DomainException toDomainException() {
        return new DomainException(this);
    }
}
