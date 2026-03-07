package com.lifelibrarians.lifebookshelf.interview.domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "conversations")
@Getter
@ToString(callSuper = true, exclude = {"interview"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Conversation {

	/* 고유 정보 { */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Long id;

	@Lob
	@Column(nullable = false)
	private String content;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private ConversationType conversationType;

	@Column(columnDefinition = "json")
	private String materials;

	@Column(nullable = false)
	private LocalDateTime createdAt;
	/* } 고유 정보 */

	/* 연관 정보 { */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "interview_id", nullable = false)
	private Interview interview;
	/* } 연관 정보 */

	/* 생성자 (V1) { */
	/**
	 * V1용 생성자 – materials 없이 생성
	 */
	protected Conversation(
			String content,
			ConversationType conversationType,
			Interview interview,
			LocalDateTime createdAt
	) {
		this.content = content;
		this.conversationType = conversationType;
		this.interview = interview;
		this.createdAt = createdAt;
	}

	/**
	 * V1용 팩토리 – 기존 코드 그대로 사용 가능
	 */
	public static Conversation of(
			String content,
			ConversationType conversationType,
			Interview interview,
			LocalDateTime createdAt
	) {
		return new Conversation(content, conversationType, interview, createdAt);
	}
	/* } 생성자 (V1) */

	/* 생성자 (V2) { */
	/**
	 * V2용 생성자 – materials 포함
	 */
	protected Conversation(
			String content,
			ConversationType conversationType,
			String materials,
			Interview interview,
			LocalDateTime createdAt
	) {
		this.content = content;
		this.conversationType = conversationType;
		this.materials = materials;
		this.interview = interview;
		this.createdAt = createdAt;
	}

	/**
	 * V2용 팩토리 – 신규 materials 필드까지 설정
	 */
	public static Conversation ofV2(
			String content,
			ConversationType conversationType,
			String materials,
			Interview interview,
			LocalDateTime createdAt
	) {
		return new Conversation(content, conversationType, materials, interview, createdAt);
	}
	/* } 생성자 (V2) */

	/* 업데이트 메소드 (선택) { */
    public void updateInterview(Interview interview) {
        this.interview = interview;
    }

	/**
	 * V2용 materials만 갱신
	 */
	public void updateMaterialsV2(String materials) {
		this.materials = materials;
	}
	/* } 업데이트 메소드 */
}
