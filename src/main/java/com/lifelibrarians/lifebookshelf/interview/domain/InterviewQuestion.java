package com.lifelibrarians.lifebookshelf.interview.domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "interview_questions")
@Getter
@ToString(callSuper = true, exclude = {"interview"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InterviewQuestion {

	/* 고유 정보 { */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Long id;

	@Column(name = "question_order", nullable = false)
	private Integer questionOrder;

	@Lob
	@Column(nullable = false)
	private String questionText;

	@Column(columnDefinition = "json")
	private String materials;
/*maria DB
* JSON == LONGTEXT 라고 생각하면 되고, 앱에서는 String 으로 받아서 Jackson / Gson으로 파싱해서 쓰면 됨
* */
	@Column(nullable = false)
	private LocalDateTime createdAt;
	/* } 고유 정보 */

	/* 연관 정보 { */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "interview_id", nullable = false)
	private Interview interview;
	/* } 연관 정보 */

	/* 생성자 (V1) { */
	protected InterviewQuestion(
			Integer questionOrder,
			String questionText,
			LocalDateTime createdAt,
			Interview interview
	) {
		this.questionOrder = questionOrder;
		this.questionText = questionText;
		this.createdAt = createdAt;
		this.interview = interview;
	}
	public static InterviewQuestion of(
			Integer questionOrder,
			String questionText,
			LocalDateTime createdAt,
			Interview interview
	) {
		return new InterviewQuestion(questionOrder, questionText, createdAt, interview);
	}
	/* } 생성자 (V1) */

	/* 생성자 (V2) { */
	protected InterviewQuestion(
			Integer questionOrder,
			String questionText,
			String materials,
			LocalDateTime createdAt,
			Interview interview
	) {
		this.questionOrder = questionOrder;
		this.questionText = questionText;
		this.materials = materials;
		this.createdAt = createdAt;
		this.interview = interview;
	}

	public static InterviewQuestion ofV2(
			Integer questionOrder,
			String questionText,
			String materials,
			LocalDateTime createdAt,
			Interview interview
	) {
		return new InterviewQuestion(questionOrder, questionText, materials, createdAt, interview);
	}
	/* } 생성자 (V2) */

	/* 기타 메소드 { */

	public void setInterview(Interview interview) {
		this.interview = interview;
	}

    /**
	 * V2: materials 갱신
	 */
	public void updateMaterialsV2(String materials) {
		this.materials = materials;
	}
	/* } 기타 메소드 */
}
