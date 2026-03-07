package com.lifelibrarians.lifebookshelf.exception.status;

import com.lifelibrarians.lifebookshelf.exception.ControllerException;
import com.lifelibrarians.lifebookshelf.exception.DomainException;
import com.lifelibrarians.lifebookshelf.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public enum MemberExceptionStatus implements ExceptionStatus {

	MEMBER_NAME_LENGTH_EXCEEDED(400, "MEMBER001", "이름은 최소 2자, 최대 64자까지 입력할 수 있습니다."),
	MEMBER_METADATA_NOT_FOUND(404, "MEMBER002", "회원 메타데이터가 존재하지 않습니다."),
	MEMBER_NICKNAME_LENGTH_EXCEEDED(400, "MEMBER003", "닉네임은 최소 2자, 최대 64자까지 입력할 수 있습니다.");

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
