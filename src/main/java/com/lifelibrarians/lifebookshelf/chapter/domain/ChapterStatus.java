package com.lifelibrarians.lifebookshelf.chapter.domain;

import com.lifelibrarians.lifebookshelf.member.domain.Member;
import javax.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Deprecated
@Entity
@Table(name = "chapter_statuses")
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChapterStatus {

	/* 고유 정보 { */
	@Id
	@Column(nullable = false, updatable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private LocalDateTime updatedAt;
	/* } 고유 정보 */

	/* 연관 정보 { */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "current_chapter_id", nullable = false)
	private Chapter currentChapter;
	/* } 연관 정보 */

	/* 생성자 { */
	protected ChapterStatus(LocalDateTime updatedAt, Member member, Chapter currentChapter) {
		this.updatedAt = updatedAt;
		this.member = member;
		this.currentChapter = currentChapter;
	}

	public static ChapterStatus of(LocalDateTime updatedAt, Member member, Chapter currentChapter) {
		return new ChapterStatus(updatedAt, member, currentChapter);
	}

	/* } 생성자 */

	public void updateChapter(Chapter chapter, LocalDateTime now) {
		this.currentChapter = chapter;
		this.updatedAt = now;
	}


	public void setChapter(Chapter chapter) {
		this.currentChapter = chapter;
	}
}
