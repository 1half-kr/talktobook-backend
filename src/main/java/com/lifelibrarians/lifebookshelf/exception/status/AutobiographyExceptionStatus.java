package com.lifelibrarians.lifebookshelf.exception.status;

import com.lifelibrarians.lifebookshelf.exception.ControllerException;
import com.lifelibrarians.lifebookshelf.exception.DomainException;
import com.lifelibrarians.lifebookshelf.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public enum AutobiographyExceptionStatus implements ExceptionStatus {

	CHAPTER_ALREADY_EXISTS(409, "BIO001", "이미 존재하는 챕터입니다."),
	CHAPTER_NAME_LENGTH_EXCEEDED(400, "BIO002", "챕터 이름은 비어있을 수 없으며, 최대 64자까지 입력할 수 있습니다."),
	CHAPTER_NOT_FOUND(404, "BIO003", "챕터 ID가 존재하지 않습니다."),
	CHAPTER_NOT_OWNER(403, "BIO004", "해당 챕터의 주인이 아닙니다."),
	AUTOBIOGRAPHY_TITLE_LENGTH_EXCEEDED(400, "BIO005", "자서전 제목은 비어있을 수 없으며, 최대 64자까지 입력할 수 있습니다."),
	AUTOBIOGRAPHY_CONTENT_LENGTH_EXCEEDED(400, "BIO006",
			"자서전 내용은 비어있을 수 없으며, 최대 30000자까지 입력할 수 있습니다."),
	AUTOBIOGRAPHY_NOT_FOUND(404, "BIO008", "자서전 ID가 존재하지 않습니다."),
	AUTOBIOGRAPHY_NOT_OWNER(403, "BIO009", "해당 자서전의 주인이 아닙니다."),
	CHAPTER_ALREADY_HAS_AUTOBIOGRAPHY(
			409, "BIO010", "해당 챕터는 이미 다른 자서전을 가지고 있습니다."),
	SUBCHAPTER_NUMBER_INVALID(400, "BIO011", "챕터의 서브챕터의 번호가 부모 챕터의 번호로 시작해야 합니다."),
	CHAPTER_NUMBER_FORMAT_INVALID(400, "BIO012", "챕터 번호는 1, 1.1, 1.1.1과 같은 형식이어야 합니다."),
	CHAPTER_SIZE_EXCEEDED(400, "BIO013", "챕터는 최소 1개, 최대 16개까지 생성할 수 있습니다."),
	CHAPTER_NUMBER_DUPLICATED(400, "BIO014", "챕터 번호는 중복될 수 없습니다."),
	NEXT_CHAPTER_NOT_FOUND(404, "BIO015", "다음 챕터가 존재하지 않습니다."),
	CHAPTER_DESCRIPTION_LENGTH_EXCEEDED(400, "BIO016", "챕터 설명은 비어있을 수 없으며, 최대 64자까지 입력할 수 있습니다."),
	AUTOBIOGRAPHY_NOT_EXISTS(404, "BIO017", "자서전이 존재하지 않습니다. 자서전을 먼저 생성해주세요."),
	CHAPTER_NOT_EXISTS(404, "BIO018", "챕터가 존재하지 않습니다. 챕터 목록을 먼저 생성해주세요."),

    AUTOBIOGRAPHY_REASON_LENGTH_EXCEEDED(400, "BIO019", "자서전 생성 이유는 최대 500자까지 입력할 수 있습니다."),

    AUTOBIOGRAPHY_STATUS_NOT_FOUND(404, "BIO020", "연결된 자서전이 없습니다."),

    AUTOBIOGRAPHY_PROGRESSING_STATUS_NOT_FOUND(404, "BIO021", "PROGRESSING 상태의 자서이 아닙니다."),
    AUTOBIOGRAPHY_ENOUTH_STATUS_NOT_FOUND(404, "BIO022", "ENOUGH 상태의 자서전이 아닙니다."),

    THEME_NOT_FOUND(404, "BIO023", "해당 자서전 테마를 찾을 수 없습니다."),

    AUTOBIOGRAPHY_CHAPTER_NOT_FOUND(404, "BIO024", "자서전 챕터를 찾을 수 없습니다.")
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
