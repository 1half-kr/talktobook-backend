package com.lifelibrarians.lifebookshelf.exception.status;

import com.lifelibrarians.lifebookshelf.exception.ControllerException;
import com.lifelibrarians.lifebookshelf.exception.DomainException;
import com.lifelibrarians.lifebookshelf.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public enum AuthExceptionStatus implements ExceptionStatus {
	INVALID_EMAIL_FORMAT(400, "AUTH001", "이메일 형식이 올바른지 다시 확인해주세요. 옳은 예: john.doe@example.com"),
	EMAIL_TOO_LONG(400, "AUTH002", "이메일은 비어있을 수 없으며, 최대 64자까지 입력할 수 있습니다."),
	PASSWORD_FORMAT_ERROR(400, "AUTH003",
			"비밀번호는 영문, 숫자, 특수문자를 포함하여 최소 8자이상, 최대 64자이하까지 입력할 수 있습니다."),
	EMAIL_OR_PASSWORD_INCORRECT(401, "AUTH006", "이메일 또는 비밀번호가 일치하지 않습니다."),
	INVALID_AUTH_CODE(403, "AUTH005", "인증 코드가 유효하지 않습니다."),
    NOT_FOUND_EMAIL(404, "AUTH011", "해당 이메일을 찾을 수 없습니다."),
	EMAIL_NOT_VERIFIED(403, "AUTH007", "이메일 인증이 완료되지 않은 사용자입니다."),
	MEMBER_NOT_FOUND(404, "AUTH008", "존재하지 않는 회원입니다."),
	MEMBER_ALREADY_EXISTS(409, "AUTH004", "이미 존재하는 회원입니다."),
	MEMBER_ALREADY_WITHDRAWN(410, "AUTH009", "이미 탈퇴한 회원입니다."),
	MEMBER_IS_NOT_ADMIN(403, "AUTH010", "관리자 권한이 없는 회원입니다."),

    REFRESH_TOKEN_EXPIRED(401, "AUTH012", "리프레시 토큰이 만료되었습니다."),
    INVALID_REFRESH_TOKEN(401, "AUTH013", "리프레시 토큰이 유효하지 않습니다.")
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