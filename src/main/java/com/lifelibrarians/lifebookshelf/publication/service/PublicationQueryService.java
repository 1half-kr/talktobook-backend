package com.lifelibrarians.lifebookshelf.publication.service;

import com.lifelibrarians.lifebookshelf.chapter.domain.Chapter;
import com.lifelibrarians.lifebookshelf.chapter.repository.ChapterRepository;
import com.lifelibrarians.lifebookshelf.community.book.domain.Book;
import com.lifelibrarians.lifebookshelf.community.book.domain.BookChapter;
import com.lifelibrarians.lifebookshelf.community.book.domain.BookContent;
import com.lifelibrarians.lifebookshelf.community.book.repository.BookRepository;
import com.lifelibrarians.lifebookshelf.exception.status.AuthExceptionStatus;
import com.lifelibrarians.lifebookshelf.exception.status.CommunityExceptionStatus;
import com.lifelibrarians.lifebookshelf.exception.status.PublicationExceptionStatus;
import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.mapper.PublicationMapper;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import com.lifelibrarians.lifebookshelf.member.repository.MemberRepository;
import com.lifelibrarians.lifebookshelf.publication.domain.Publication;
import com.lifelibrarians.lifebookshelf.publication.dto.response.PublicationListResponseDto;
import com.lifelibrarians.lifebookshelf.publication.dto.response.PublicationPreviewDto;
import com.lifelibrarians.lifebookshelf.publication.dto.response.PublicationProgressResponseDto;
import com.lifelibrarians.lifebookshelf.publication.repository.PublicationRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Logging
public class PublicationQueryService {

	private final PublicationRepository publicationRepository;
	private final ChapterRepository chapterRepository;
	private final PublicationMapper publicationMapper;
	private final MemberRepository memberRepository;
	private final BookRepository bookRepository;

	public PublicationListResponseDto getMyPublications(Long memberId, Pageable pageable) {
		Page<Publication> publications = publicationRepository.getAllByMemberId(memberId, pageable);
		List<PublicationPreviewDto> publicationPreviewDtos = publications.stream()
				.map(publication -> {
					BookContent bookContent = Optional.ofNullable(publication.getBook().getBookChapters())
							.filter(chapters -> !chapters.isEmpty())
							.map(chapters -> chapters.get(0))
							.map(BookChapter::getBookContents)
							.filter(contents -> !contents.isEmpty())
							.map(contents -> contents.get(0))
							.orElse(null);
					return publicationMapper.toPublicationPreviewDto(publication, bookContent);
				})
				.filter(Objects::nonNull)
				.collect(Collectors.toList());

		return publicationMapper.toPublicationListResponseDto(
				publicationPreviewDtos,
				pageable.getPageNumber(),
				(int) publications.getTotalElements(),
				publications.getTotalPages(),
				publications.hasNext(),
				publications.hasPrevious()
		);
	}

	public List<Chapter> getChaptersWithAutobiography(Long memberId) {
		return chapterRepository.findAllByMemberIdByParentChapterIdIsNotNullWithAutobiography(memberId);
	}

	public Member getMember(Long memberId) {
		return memberRepository.findById(memberId)
				.orElseThrow(AuthExceptionStatus.MEMBER_NOT_FOUND::toServiceException);
	}

	public PublicationProgressResponseDto getPublicationProgress(Long memberId, Long publicationId) {
		Member member = getMember(memberId);
		Publication publication = publicationRepository.findByMemberIdWithBook(publicationId, memberId)
				.orElseThrow(PublicationExceptionStatus.PUBLICATION_NOT_FOUND::toServiceException);
		if (!publication.getBook().getMember().equals(member)) {
			throw PublicationExceptionStatus.PUBLICATION_NOT_OWNER.toServiceException();
		}
		return publicationMapper.toPublicationProgressResponseDto(publication);
	}

	public Book getBook(Long bookId) {
		return bookRepository.findById(bookId)
				.orElseThrow(CommunityExceptionStatus.BOOK_NOT_FOUND::toServiceException);
	}

    // CoShow용 S3 pdf 조회
}
