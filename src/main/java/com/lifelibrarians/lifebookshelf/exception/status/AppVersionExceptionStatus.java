package com.lifelibrarians.lifebookshelf.exception.status;

import com.lifelibrarians.lifebookshelf.exception.ControllerException;
import com.lifelibrarians.lifebookshelf.exception.DomainException;
import com.lifelibrarians.lifebookshelf.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public enum AppVersionExceptionStatus implements ExceptionStatus {
    APP_VERSION_NOT_FOUND(404, "APP_VERSION_001", "앱 버전을 찾을 수 없습니다."),
    APP_VERSION_IS_DEPRECATED(426, "APP_VERSION_002", "앱 버전이 더 이상 지원되지 않습니다. 업데이트가 필요합니다."),
    MEMBER_APP_VERSION_NOT_FOUND(404, "APP_VERSION_003", "회원의 앱 버전 정보를 찾을 수 없습니다."),

    INVALID_PLATFORM_TYPE(400, "APP_VERSION_004", "유효하지 않은 플랫폼 타입입니다.")
    ;

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
