package com.lifelibrarians.lifebookshelf.mapper;

import com.lifelibrarians.lifebookshelf.community.book.domain.BookContent;
import com.lifelibrarians.lifebookshelf.image.service.ImageService;
import com.lifelibrarians.lifebookshelf.publication.domain.Publication;
import com.lifelibrarians.lifebookshelf.publication.dto.response.PublicationListResponseDto;
import com.lifelibrarians.lifebookshelf.publication.dto.response.PublicationPreviewDto;
import com.lifelibrarians.lifebookshelf.publication.dto.response.PublicationProgressResponseDto;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class PublicationMapper {

	@Autowired
	protected ImageService imageService;

	@Named("mapImageUrl")
	protected String mapImageUrl(String profileImageUrl) {
		return imageService.getImageUrl(profileImageUrl);
	}

	@Mapping(source = "publication.id", target = "publicationId")
	@Mapping(source = "publication.book.id", target = "bookId")
	@Mapping(source = "publication.book.title", target = "title")
	@Mapping(source = "bookContent.pageContent", target = "contentPreview", qualifiedByName = "truncate")
	@Mapping(source = "publication.book.coverImageUrl", target = "coverImageUrl", qualifiedByName = "mapImageUrl")
	@Mapping(source = "publication.book.visibleScope", target = "visibleScope")
	@Mapping(source = "publication.book.page", target = "page")
	@Mapping(source = "publication.book.createdAt", target = "createdAt")
	public abstract PublicationPreviewDto toPublicationPreviewDto(Publication publication,
			BookContent bookContent);

	@Named("truncate")
	String truncateContent(String content) {
		return content != null && content.length() > 16 ? content.substring(0, 16).concat("...")
				: content;
	}

	@Mapping(source = "publicationPreviewDtos", target = "results")
	public abstract PublicationListResponseDto toPublicationListResponseDto(
			List<PublicationPreviewDto> publicationPreviewDtos,
			int currentPage,
			int totalElements,
			int totalPages,
			boolean hasNextPage,
			boolean hasPreviousPage
	);

	@Mapping(source = "publication.id", target = "publicationId")
	@Mapping(source = "publication.book.id", target = "bookId")
	@Mapping(source = "publication.book.title", target = "title")
	@Mapping(source = "publication.book.coverImageUrl", target = "coverImageUrl", qualifiedByName = "mapImageUrl")
	@Mapping(source = "publication.book.visibleScope", target = "visibleScope")
	@Mapping(source = "publication.book.page", target = "page")
	@Mapping(source = "publication.book.createdAt", target = "createdAt")
	@Mapping(source = "publication.price", target = "price")
	@Mapping(source = "publication.titlePosition", target = "titlePosition")
	@Mapping(source = "publication.publishStatus", target = "publishStatus")
	@Mapping(source = "publication.requestedAt", target = "requestedAt")
	@Mapping(source = "publication.willPublishedAt", target = "willPublishedAt")
	public abstract PublicationProgressResponseDto toPublicationProgressResponseDto(
			Publication publication);
}
