package com.lifelibrarians.lifebookshelf.community.comment.domain;

import com.lifelibrarians.lifebookshelf.community.book.domain.Book;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import javax.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "comments")
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

	/* 고유 정보 { */
	@Id
	@Column(nullable = false, updatable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private Long parentCommentId;

	@Lob
	private String content;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	@Column
	private LocalDateTime deletedAt;
	/* } 고유 정보 */

	/* 연관 정보 { */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_id", nullable = false)
	private Book book;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "author_member_id", nullable = false)
	private Book authorMember;
	/* } 연관 정보 */

	/* 생성자 { */
	protected Comment(Long parentCommentId, String content, LocalDateTime createdAt,
			LocalDateTime updatedAt, LocalDateTime deletedAt) {
		this.parentCommentId = parentCommentId;
		this.content = content;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.deletedAt = deletedAt;
	}

	public static Comment of(Long parentCommentId, String content, LocalDateTime createdAt,
			LocalDateTime updatedAt, LocalDateTime deletedAt) {
		return new Comment(parentCommentId, content, createdAt, updatedAt, deletedAt);
	}
	/* } 생성자 */
}
