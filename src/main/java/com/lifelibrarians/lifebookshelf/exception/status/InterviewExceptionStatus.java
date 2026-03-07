package com.lifelibrarians.lifebookshelf.exception.status;

import com.lifelibrarians.lifebookshelf.exception.ControllerException;
import com.lifelibrarians.lifebookshelf.exception.DomainException;
import com.lifelibrarians.lifebookshelf.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public enum InterviewExceptionStatus implements ExceptionStatus {

	INTERVIEW_NOT_FOUND(404, "INTERVIEW001", "인터뷰 ID가 존재하지 않습니다."),
	INTERVIEW_NOT_OWNER(403, "INTERVIEW002", "해당 인터뷰의 주인이 아닙니다."),
	INTERVIEW_MAX_CONVERSATIONS_EXCEEDED(400, "INTERVIEW003", "대화는 최소 1개, 최대 20개까지만 요청할 수 있습니다."),
	INTERVIEW_CONTENT_LENGTH_EXCEEDED(400, "INTERVIEW004", "대화 내용은 최소 1자, 최대 512자까지 입력할 수 있습니다."),
	INTERVIEW_QUESTION_TEXT_LENGTH_EXCEEDED(400, "INTERVIEW005",
			"인터뷰 질문의 텍스트는 비어있을 수 없으며, 최대 64자까지 입력할 수 있습니다."),
	INTERVIEW_QUESTION_NOT_FOUND(404, "INTERVIEW006", "인터뷰 질문 ID가 존재하지 않습니다."),
	INTERVIEW_QUESTION_NOT_IN_INTERVIEW(403, "INTERVIEW007", "인터뷰 질문 ID가 해당 인터뷰에 속하지 않습니다."),
	INTERVIEW_QUESTION_UPDATE_NOT_FOUND(404, "INTERVIEW008", "갱신하려는 인터뷰 질문 ID가 존재하지 않습니다."),
	INTERVIEW_QUESTION_UPDATE_NOT_IN_INTERVIEW(404, "INTERVIEW009",
			"갱신하려는 인터뷰 질문 ID가 해당 인터뷰에 속하지 않습니다."),
	INTERVIEW_QUESTION_ORDER_DUPLICATED(400, "INTERVIEW010", "인터뷰 질문의 순서는 중복될 수 없습니다."),
	NEXT_INTERVIEW_QUESTION_NOT_FOUND(404, "INTERVIEW011", "다음 질문이 존재하지 않습니다.");

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
