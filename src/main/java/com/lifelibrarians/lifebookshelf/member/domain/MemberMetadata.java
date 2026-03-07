package com.lifelibrarians.lifebookshelf.member.domain;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "member_metadatas")
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberMetadata {

	/* ====== PK ====== */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/* ====== V2 주요 필드 ====== */
	@Column(nullable = false)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private GenderType gender;

	@Column(name = "age_group")
	private String ageGroup;

	@Column
	private String occupation;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;


	/* ====== V1 legacy 필드 (read-only, deprecated) ====== */
	@Deprecated
	@Column(name = "borned_at", insertable = false, updatable = false)
	private LocalDate bornedAt;

	@Deprecated
	@Column(name = "education_level", insertable = false, updatable = false)
	private String educationLevel;

	@Deprecated
	@Column(name = "has_children", insertable = false, updatable = false)
	private Boolean hasChildren;

	@Deprecated
	@Column(name = "marital_status", insertable = false, updatable = false)
	private String maritalStatus;

	@Deprecated
	@Column(name = "updated_at", insertable = false, updatable = false)
	private LocalDateTime updatedAt;


	/* ====== Member 연관관계 ====== */
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;


	/* ====== V1 full 생성자 (deprecated) ====== */
	@Deprecated
	protected MemberMetadata(
			String name,
			LocalDate bornedAt,
			GenderType gender,
			Boolean hasChildren,
			String occupation,
			String educationLevel,
			String maritalStatus,
			LocalDateTime createdAt,
			LocalDateTime updatedAt,
			LocalDateTime deletedAt,
			Member member
	) {
		this.name = name;
		this.bornedAt = bornedAt;
		this.gender = gender;
		this.hasChildren = hasChildren;
		this.occupation = occupation;
		this.educationLevel = educationLevel;
		this.maritalStatus = maritalStatus;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.member = member;
	}

	@Deprecated
	public void update(
			String name,
			LocalDate bornedAt,
			GenderType gender,
			Boolean hasChildren,
			String occupation,
			String educationLevel,
			String maritalStatus
	) {
		this.name = name;
		this.bornedAt = bornedAt;
		this.gender = gender;
		this.hasChildren = hasChildren;
		this.occupation = occupation;
		this.educationLevel = educationLevel;
		this.maritalStatus = maritalStatus;
		this.updatedAt = LocalDateTime.now();
	}
	/* ====== V2 생성자 ====== */
	protected MemberMetadata(
			String name,
			GenderType gender,
			String ageGroup,
			String occupation,
			Member member
	) {
		this.name = name;
		this.gender = gender;
		this.ageGroup = ageGroup;
		this.occupation = occupation;
		this.member = member;
		this.createdAt = LocalDateTime.now();
	}


	/* ====== 팩토리 메서드 ====== */
	@Deprecated
	public static MemberMetadata of(
			String name,
			LocalDate bornedAt,
			GenderType gender,
			Boolean hasChildren,
			String occupation,
			String educationLevel,
			String maritalStatus,
			LocalDateTime createdAt,
			LocalDateTime updatedAt,
			LocalDateTime deletedAt,
			Member member
	) {
		return new MemberMetadata(
				name, bornedAt, gender, hasChildren,
				occupation, educationLevel, maritalStatus,
				createdAt, updatedAt, deletedAt,
				member
		);
	}

	/** V2 전용 생성 */
	public static MemberMetadata ofV2(
			String name,
			GenderType gender,
			String ageGroup,
			String occupation,
			Member member
	) {
		return new MemberMetadata(name, gender, ageGroup, occupation, member);
	}

	/** V2 업데이트 */
	public void updateV2(String name, GenderType gender, String ageGroup, String occupation) {
		this.name = name;
		this.gender = gender;
		this.ageGroup = ageGroup;
		this.occupation = occupation;
	}

	public void setMember(Member member) {
		this.member = member;
	}
}
