package com.lifelibrarians.lifebookshelf.publication.service;

import com.lifelibrarians.lifebookshelf.autobiography.domain.Autobiography;
import com.lifelibrarians.lifebookshelf.chapter.domain.Chapter;
import com.lifelibrarians.lifebookshelf.community.book.domain.Book;
import com.lifelibrarians.lifebookshelf.community.book.domain.BookChapter;
import com.lifelibrarians.lifebookshelf.community.book.domain.BookContent;
import com.lifelibrarians.lifebookshelf.community.book.domain.VisibleScope;
import com.lifelibrarians.lifebookshelf.community.book.repository.BookChapterRepository;
import com.lifelibrarians.lifebookshelf.community.book.repository.BookContentRepository;
import com.lifelibrarians.lifebookshelf.community.book.repository.BookRepository;
import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import com.lifelibrarians.lifebookshelf.publication.domain.Publication;
import com.lifelibrarians.lifebookshelf.event.PublicationCreatedEvent;
import com.lifelibrarians.lifebookshelf.publication.domain.PublishStatus;
import com.lifelibrarians.lifebookshelf.publication.dto.request.PublicationCreateRequestDto;
import com.lifelibrarians.lifebookshelf.publication.repository.PublicationRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Logging
public class PublicationCommandService {

	private final PublicationRepository publicationRepository;
	private final BookRepository bookRepository;
	private final EntityManager entityManager;
	private final BookChapterRepository bookChapterRepository;
	private final BookContentRepository bookContentRepository;
	private final ApplicationEventPublisher eventPublisher;


	private int calculateTotalPages(List<Chapter> chapters, int charactersPerPage) {
		long totalTextLength = 0;

		for (Chapter chapter : chapters) {
			if (chapter.getChapterAutobiography() != null) {
				Autobiography autobiography = chapter.getChapterAutobiography();
				if (autobiography.getTitle() != null) {
					totalTextLength += autobiography.getTitle().length();
				}
				if (autobiography.getContent() != null) {
					totalTextLength += autobiography.getContent().length();
				}
			}
		}

		return (int) Math.ceil((double) totalTextLength / charactersPerPage);
	}

	private List<String> divideTextIntoPages(String text, int charactersPerPage) {
		List<String> pages = new ArrayList<>();
		int start = 0;
		while (start < text.length()) {
			int end = Math.min(start + charactersPerPage, text.length());
			pages.add(text.substring(start, end));
			start = end;
		}
		return pages;
	}

	public void deleteAllRelatedData(List<Long> chapterIds, Long memberId) {
		// Ensure all references in 'Interviews' to 'InterviewQuestions' are removed
		String nullifyCurrentQuestion =
				"UPDATE Interview i SET i.currentQuestion = null WHERE i.currentQuestion.id IN " +
						"(SELECT iq.id FROM InterviewQuestion iq WHERE iq.interview.chapter.id IN (:chapterIds))";
		entityManager.createQuery(nullifyCurrentQuestion)
				.setParameter("chapterIds", chapterIds)
				.executeUpdate();

		// Delete Conversations
		String deleteConversations = "DELETE FROM Conversation c WHERE c.interview.id IN " +
				"(SELECT i.id FROM Interview i WHERE i.chapter.id IN (:chapterIds))";
		entityManager.createQuery(deleteConversations)
				.setParameter("chapterIds", chapterIds)
				.executeUpdate();

		// Delete Interview Questions
		String deleteQuestions = "DELETE FROM InterviewQuestion iq WHERE iq.interview.id IN " +
				"(SELECT i.id FROM Interview i WHERE i.chapter.id IN (:chapterIds))";
		entityManager.createQuery(deleteQuestions)
				.setParameter("chapterIds", chapterIds)
				.executeUpdate();

		// Delete Interviews
		String deleteInterviews = "DELETE FROM Interview i WHERE i.chapter.id IN (:chapterIds)";
		entityManager.createQuery(deleteInterviews)
				.setParameter("chapterIds", chapterIds)
				.executeUpdate();

		// Delete Autobiographies
		String deleteAutobiographies = "DELETE FROM Autobiography a WHERE a.chapter.id IN (:chapterIds)";
		entityManager.createQuery(deleteAutobiographies)
				.setParameter("chapterIds", chapterIds)
				.executeUpdate();

		// Delete Chapter Statuses
		String deleteChapterStatuses = "DELETE FROM ChapterStatus cs WHERE cs.currentChapter.id IN (:chapterIds)";
		entityManager.createQuery(deleteChapterStatuses)
				.setParameter("chapterIds", chapterIds)
				.executeUpdate();

		// Delete Chapters
		String deleteChapters = "DELETE FROM Chapter ch WHERE ch.id IN (:chapterIds)";
		entityManager.createQuery(deleteChapters)
				.setParameter("chapterIds", chapterIds)
				.executeUpdate();

		// Delete Member Chapters (Root Chapter)
		String deleteMemberChapters = "DELETE FROM Chapter c WHERE c.member.id = :memberId";
		entityManager.createQuery(deleteMemberChapters)
				.setParameter("memberId", memberId)
				.executeUpdate();
	}

	public void createPublication(Member member, PublicationCreateRequestDto requestDto,
			List<Chapter> chapters) {

		// TODO: 총 페이지 수 로직을 개선해야 합니다.
		int totalPages = calculateTotalPages(chapters, 2200);
		LocalDateTime now = LocalDateTime.now();
		Book book = Book.of(
				requestDto.getTitle(),
				requestDto.getPreSignedCoverImageUrl(),
				VisibleScope.PRIVATE,
				totalPages,
				now,
				null,
				member
		);
		bookRepository.save(book);

		List<BookChapter> bookChapters = new ArrayList<>();
		for (Chapter chapter : chapters) {
			BookChapter bookChapter = BookChapter.of(chapter.getNumber(), chapter.getName());
			bookChapters.add(bookChapter);
			book.getBookChapters().add(bookChapter);
			bookChapter.setBook(book);

			if (chapter.getChapterAutobiography() != null) {
				Autobiography autobiography = chapter.getChapterAutobiography();
				List<String> pages = divideTextIntoPages(autobiography.getContent(), 2200);
				int pageNumber = 1;
				for (String page : pages) {
					BookContent bookContent = BookContent.of(pageNumber++, page, bookChapter);
					bookChapter.getBookContents().add(bookContent);
					bookContent.setBookChapter(bookChapter);
				}
			}
		}
		bookRepository.save(book);
		bookChapterRepository.saveAll(bookChapters);
		bookContentRepository.saveAll(bookChapters.stream()
				.flatMap(bookChapter -> bookChapter.getBookContents().stream())
				.collect(Collectors.toList()));

		// FIXME: 추후 비용 계산 로직을 적용해야 합니다.
		// FIXME: 추후 예상 출판일 계산 로직을 적용해야 합니다.
		Publication publication = Publication.of(
				100 * totalPages,
				requestDto.getTitlePosition(),
				PublishStatus.REQUESTED,
				now,
				now.plusDays(14),
				null,
				null,
				book
		);
		publicationRepository.save(publication);

		eventPublisher.publishEvent(new PublicationCreatedEvent(publication.getId()));

		deleteAllRelatedData(chapters.stream().map(Chapter::getId).collect(Collectors.toList()),
				member.getId());
	}

	public void deleteBook(Book book) {
		bookRepository.delete(book);
	}
}
