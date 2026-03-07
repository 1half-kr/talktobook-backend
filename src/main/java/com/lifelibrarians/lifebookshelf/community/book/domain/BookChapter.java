package com.lifelibrarians.lifebookshelf.community.book.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Entity
@Table(name = "bookChapters")
@Getter
@ToString(callSuper = true, exclude = {"bookContents"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookChapter {

	/* 고유 정보 { */
	@Id
	@Column(nullable = false, updatable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String number;

	@Column(nullable = false)
	private String name;
	/* } 고유 정보 */

	/* 연관 정보 { */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_id", nullable = false)
	private Book book;

	@OneToMany(mappedBy = "bookChapter")
	private List<BookContent> bookContents = new ArrayList<>();
	/* } 연관 정보 */

	/* 생성자 { */
	protected BookChapter(String number, String name) {
		this.number = number;
		this.name = name;
	}

	public static BookChapter of(String number, String name) {
		return new BookChapter(number, name);
	}

	public void setBook(Book book) {
		this.book = book;
	}
	/* } 생성자 */
}
