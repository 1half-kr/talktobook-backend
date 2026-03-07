package com.lifelibrarians.lifebookshelf.utils.exception;

import com.lifelibrarians.lifebookshelf.exception.ControllerException;
import com.lifelibrarians.lifebookshelf.exception.DomainException;
import com.lifelibrarians.lifebookshelf.exception.ServiceException;
import com.lifelibrarians.lifebookshelf.exception.status.ErrorReason;
import com.lifelibrarians.lifebookshelf.exception.status.ExceptionStatus;
import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public enum UtilsExceptionStatus implements ExceptionStatus {

	INVALID_FILE(400, "UTILS001", "올바르지 않은 파일입니다."),
	INVALID_FILE_EXTENSION(400, "UTILS002", "올바르지 않은 확장자입니다."),
	INVALID_FILE_URL(400, "UTILS003", "올바르지 않은 파일 경로입니다."),
	INVALID_IMAGE_TYPE(400, "UTILS004", "올바르지 않은 이미지 타입입니다."),
	NON_MAPPED_TARGET(500, "UTILS005", "매핑되지 않은 타겟입니다."),
	NON_MAPPED_FIELD(500, "UTILS006", "매핑되지 않은 필드입니다."),
	STORAGE_UPLOAD_ERROR(502, "UTILS007", "스토리지 파일 업로드 요청 중 오류가 발생했습니다."),
	STORAGE_DELETE_ERROR(502, "UTILS008", "스토리지 파일 삭제 요청 중 오류가 발생했습니다.");

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
