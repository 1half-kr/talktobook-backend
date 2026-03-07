package com.lifelibrarians.lifebookshelf.chapter.domain;

import com.lifelibrarians.lifebookshelf.autobiography.domain.Autobiography;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import javax.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Deprecated
@Entity
@Table(name = "chapters")
@Getter
@ToString(callSuper = true, exclude = {"chapterAutobiography", "member",
		"currentChapterChapterStatus"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chapter {

	/* 고유 정보 { */
	@Id
	@Column(nullable = false, updatable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private Long parentChapterId;

	@Column(nullable = false)
	@Setter
	private String number;

	@Column(nullable = false)
	@Setter
	private String name;

	@Column(nullable = false)
	@Setter
	private String description;

	@Column(nullable = false)
	@Setter
	private LocalDateTime createdAt;
	/* } 고유 정보 */

	/* 연관 정보 { */
	@OneToOne(mappedBy = "chapter")
	private Autobiography chapterAutobiography;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@OneToOne(mappedBy = "currentChapter")
	private ChapterStatus currentChapterChapterStatus;
	/* } 연관 정보 */

	/* 생성자 { */
	protected Chapter(String number, String name, String description, LocalDateTime createdAt,
			Long parentChapterId,
			Member member) {
		this.number = number;
		this.name = name;
		this.description = description;
		this.createdAt = createdAt;
		this.parentChapterId = parentChapterId;
		this.member = member;
	}

	public static Chapter of(String number, String name, String description, LocalDateTime createdAt,
			Long parentChapterId, Member member) {
		return new Chapter(number, name, description, createdAt, parentChapterId, member);
	}

	public void setChapterAutobiography(Autobiography chapterAutobiography) {
		this.chapterAutobiography = chapterAutobiography;
	}
	/* } 생성자 */
}
