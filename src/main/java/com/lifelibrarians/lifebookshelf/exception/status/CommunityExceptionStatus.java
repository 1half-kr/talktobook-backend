package com.lifelibrarians.lifebookshelf.exception.status;

import com.lifelibrarians.lifebookshelf.exception.ControllerException;
import com.lifelibrarians.lifebookshelf.exception.DomainException;
import com.lifelibrarians.lifebookshelf.exception.ServiceException;
import com.lifelibrarians.lifebookshelf.exception.status.ExceptionStatus;
import com.lifelibrarians.lifebookshelf.exception.status.ErrorReason;
import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public enum CommunityExceptionStatus implements ExceptionStatus {

	BOOK_NOT_FOUND(404, "COM001", "출판 책 ID가 존재하지 않습니다."),
	BOOK_NOT_AUTHENTICATED(403, "COM002", "해당 출판 책에 대한 권한이 없습니다."),
	BOOK_ALREADY_LIKED(409, "COM003", "이미 좋아요한 출판 책입니다."),
	BOOK_NOT_LIKED(409, "COM004", "좋아요한 출판된 책에 대해서만 취소 요청이 가능합니다."),
	COMMENT_LENGTH_EXCEEDED(400, "COM005", "댓글은 비어있을 수 없으며, 최대 512자까지 작성할 수 있습니다."),
	COMMENT_NOT_FOUND(404, "COM006", "댓글 ID가 존재하지 않습니다."),
	COMMENT_NOT_OWNER(403, "COM007", "해당 댓글에 대한 권한이 없습니다."),
	CHAPTER_NOT_FOUND(404, "COM008", "출판 책 챕터 ID가 존재하지 않습니다."),
	CHAPTER_NOT_AUTHENTICATED(403, "COM009", "해당 출판 책 챕터에 대한 권한이 없습니다.");

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