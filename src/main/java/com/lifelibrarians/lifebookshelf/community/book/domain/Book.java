package com.lifelibrarians.lifebookshelf.community.book.domain;

import com.lifelibrarians.lifebookshelf.community.comment.domain.Comment;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import com.lifelibrarians.lifebookshelf.publication.domain.Publication;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Entity
@Table(name = "books")
@Getter
@ToString(callSuper = true, exclude = {"bookComments", "authorMemberComments",
		"bookPublications", "bookChapters"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book {

	/* 고유 정보 { */
	@Id
	@Column(nullable = false, updatable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String coverImageUrl;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private VisibleScope visibleScope;

	@Column(nullable = false)
	private Integer page;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@Column
	private LocalDateTime deletedAt;
	/* } 고유 정보 */

	/* 연관 정보 { */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@OneToMany(mappedBy = "book")
	private Set<Comment> bookComments;

	@OneToMany(mappedBy = "authorMember")
	private Set<Comment> authorMemberComments;

	@OneToMany(mappedBy = "book")
	private Set<Publication> bookPublications;

	@OneToMany(mappedBy = "book")
	private List<BookChapter> bookChapters = new ArrayList<>();
	/* } 연관 정보 */

	/* 생성자 { */
	protected Book(String title, String coverImageUrl, VisibleScope visibleScope, Integer page,
			LocalDateTime createdAt, LocalDateTime deletedAt, Member member) {
		this.title = title;
		this.coverImageUrl = coverImageUrl;
		this.visibleScope = visibleScope;
		this.page = page;
		this.createdAt = createdAt;
		this.deletedAt = deletedAt;
		this.member = member;
	}

	public static Book of(String title, String coverImageUrl, VisibleScope visibleScope, Integer page,
			LocalDateTime createdAt, LocalDateTime deletedAt, Member member) {
		return new Book(title, coverImageUrl, visibleScope, page, createdAt, deletedAt, member);
	}

	public void setBookChapters(List<BookChapter> chapters) {
		this.bookChapters = chapters;
	}
	/* } 생성자 */
}
