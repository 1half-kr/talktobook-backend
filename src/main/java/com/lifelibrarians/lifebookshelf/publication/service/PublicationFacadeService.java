package com.lifelibrarians.lifebookshelf.publication.service;

import com.lifelibrarians.lifebookshelf.chapter.domain.Chapter;
import com.lifelibrarians.lifebookshelf.community.book.domain.Book;
import com.lifelibrarians.lifebookshelf.exception.status.CommunityExceptionStatus;
import com.lifelibrarians.lifebookshelf.exception.status.PublicationExceptionStatus;
import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import com.lifelibrarians.lifebookshelf.publication.dto.request.PublicationCreateRequestDto;
import com.lifelibrarians.lifebookshelf.publication.dto.response.PublicationListResponseDto;
import com.lifelibrarians.lifebookshelf.publication.dto.response.PublicationProgressResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Logging
public class PublicationFacadeService {

	private final PublicationQueryService publicationQueryService;
	private final PublicationCommandService publicationCommandService;

	/*-----------------------------------------READ-----------------------------------------*/
	public PublicationListResponseDto getMyPublications(Long memberId, Pageable pageable) {
		return publicationQueryService.getMyPublications(memberId, pageable);
	}

	public PublicationProgressResponseDto getPublicationProgress(Long memberId, Long publicationId) {
		return publicationQueryService.getPublicationProgress(memberId, publicationId);
	}

	/*-----------------------------------------CREATE-----------------------------------------*/
	public void requestPublication(Long memberId, PublicationCreateRequestDto requestDto) {
		List<Chapter> chapters = publicationQueryService.getChaptersWithAutobiography(memberId);
		if (chapters.isEmpty()) {
			throw PublicationExceptionStatus.NO_CHAPTERS_FOR_PUBLICATION.toServiceException();
		}
		if (chapters.stream().anyMatch(chapter -> chapter.getChapterAutobiography() == null)) {
			throw PublicationExceptionStatus.NO_AUTOBIOGRAPHY_FOR_CHAPTER.toServiceException();
		}
		Member member = publicationQueryService.getMember(memberId);
		publicationCommandService.createPublication(member, requestDto, chapters);
	}

	public void deleteBook(Long memberId, Long bookId) {
		Member member = publicationQueryService.getMember(memberId);
		Book book = publicationQueryService.getBook(bookId);
		if (!book.getMember().equals(member)) {
			throw CommunityExceptionStatus.BOOK_NOT_AUTHENTICATED.toServiceException();
		}
		publicationCommandService.deleteBook(book);
	}
}
