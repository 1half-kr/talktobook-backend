package com.lifelibrarians.lifebookshelf.community.book.domain;

import javax.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Entity
@Table(name = "bookContents")
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookContent {

	/* 고유 정보 { */
	@Id
	@Column(nullable = false, updatable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Integer pageNumber;

	@Lob
	private String pageContent;

	/* } 고유 정보 */

	/* 연관 정보 { */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_chapter_id", nullable = false)
	private BookChapter bookChapter;
	/* } 연관 정보 */

	/* 생성자 { */
	protected BookContent(
			Integer pageNumber, String pageContent, BookChapter bookChapter) {
		this.pageNumber = pageNumber;
		this.pageContent = pageContent;
		this.bookChapter = bookChapter;
	}

	public static BookContent of(Integer pageNumber, String pageContent, BookChapter bookChapter) {
		return new BookContent(pageNumber, pageContent, bookChapter);
	}

	public void setBookChapter(BookChapter bookChapter) {
		this.bookChapter = bookChapter;
	}
	/* } 생성자 */
}
