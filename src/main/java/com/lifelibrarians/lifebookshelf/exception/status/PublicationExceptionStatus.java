package com.lifelibrarians.lifebookshelf.exception.status;

import com.lifelibrarians.lifebookshelf.exception.ControllerException;
import com.lifelibrarians.lifebookshelf.exception.DomainException;
import com.lifelibrarians.lifebookshelf.exception.ServiceException;
import com.lifelibrarians.lifebookshelf.exception.status.ErrorReason;
import com.lifelibrarians.lifebookshelf.exception.status.ExceptionStatus;
import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public enum PublicationExceptionStatus implements ExceptionStatus {

	PUBLICATION_NOT_FOUND(404, "PUB001", "존재하지 않는 출판 ID입니다."),
	PUBLICATION_NOT_OWNER(403, "PUB002", "해당 출판에 대한 권한이 없습니다."),
	PUBLICATION_TITLE_LENGTH_EXCEEDED(400, "PUB003", "제목은 최소 2자, 최대 64자까지 입력할 수 있습니다."),
	NO_CHAPTERS_FOR_PUBLICATION(400, "PUB004", "출판을 위한 챕터가 존재하지 않습니다."),
	NO_AUTOBIOGRAPHY_FOR_CHAPTER(400, "PUB005", "챕터에 대한 자서전이 존재하지 않는 챕터가 있습니다. 모든 챕터에 자서전을 입력해주세요."),
	PUBLICATION_PROCESSOR_FAILED(502, "PUB006", "출판 처리 중 오류가 발생했습니다. 다시 시도해주세요.");


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
