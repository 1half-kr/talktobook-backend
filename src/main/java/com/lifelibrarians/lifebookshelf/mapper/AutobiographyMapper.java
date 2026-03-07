package com.lifelibrarians.lifebookshelf.mapper;

import com.lifelibrarians.lifebookshelf.autobiography.domain.Autobiography;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyChapter;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatus;
import com.lifelibrarians.lifebookshelf.autobiography.dto.response.*;
import com.lifelibrarians.lifebookshelf.classification.domain.Category;
import com.lifelibrarians.lifebookshelf.classification.domain.Material;
import com.lifelibrarians.lifebookshelf.image.service.ImageService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class AutobiographyMapper {

	@Autowired
	protected ImageService imageService;

	@Named("mapImageUrl")
	protected String mapImageUrl(String profileImageUrl) {
		return imageService.getImageUrl(profileImageUrl);
	}

    @Mapping(source = "autobiography.id", target = "autobiographyId")
    @Mapping(source = "autobiography.autobiographyChapters", target = "contentPreview", qualifiedByName = "chaptersToPreview")
    @Mapping(source = "autobiography.coverImageUrl", target = "coverImageUrl", qualifiedByName = "mapImageUrl")
    @Mapping(source = "autobiography.updatedAt", target = "updatedAt")
    @Mapping(source = "autobiography.createdAt", target = "createdAt")
    @Mapping(source = "status.status", target = "status")
    public abstract AutobiographyPreviewDto toAutobiographyPreviewDto(Autobiography autobiography, AutobiographyStatus status);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "order", target = "order")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "count", target = "count")
    @Mapping(target = "rank", ignore = true) // rank는 나중에 계산
    @Mapping(target = "imageUrl", ignore = true) // 이미지가 있다면 나중에 매핑
    public abstract AutobiographyMaterialResponseDto toAutobiographyMaterialResponseDto(Material material);

	@Mapping(source = "id", target = "autobiographyId")
	@Mapping(source = "autobiographyChapters", target = "chapters")
	public abstract AutobiographyDetailResponseDto toAutobiographyDetailResponseDto(Autobiography autobiography);

	@Mapping(source = "id", target = "chapterId")
	@Mapping(source = "coverImageUrl", target = "coverImageUrl", qualifiedByName = "mapImageUrl")
	public abstract AutobiographyDetailResponseDto.ChapterContent toChapterContent(AutobiographyChapter chapter);

    @Mapping(source="id", target="autobiographyId")
    public abstract AutobiographyCurrentResponseDto toAutobiographyCurrentResponseDto(Autobiography autobiography);

	@Named("truncate")
	String truncateContent(String content) {
		return content != null && content.length() > 16 ? content.substring(0, 16).concat("...")
				: content;
	}

	@Named("chaptersToPreview")
	String chaptersToPreview(Set<AutobiographyChapter> chapters) {
		if (chapters == null || chapters.isEmpty()) {
			return "";
		}
		return chapters.stream()
				.sorted((c1, c2) -> c1.getCreatedAt().compareTo(c2.getCreatedAt()))
				.map(chapter -> chapter.getTitle() + "\n" + chapter.getContent())
				.reduce((a, b) -> a + "\n" + b)
				.orElse("");
	}
}
