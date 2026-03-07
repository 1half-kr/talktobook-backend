package com.lifelibrarians.lifebookshelf.chapter.repository;

import com.lifelibrarians.lifebookshelf.chapter.domain.Chapter;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {

	@Query("SELECT c FROM Chapter c WHERE c.member.id = :memberId")
	List<Chapter> findAllByMemberId(Long memberId);

	@Query("SELECT c FROM Chapter c WHERE c.member.id = :memberId and c.parentChapterId is null")
	Page<Chapter> findByMemberIdAndParentChapterIdIsNull(Long memberId, Pageable pageable);

	@Query("SELECT c FROM Chapter c WHERE c.parentChapterId = :parentChapterId")
	List<Chapter> findByParentChapterId(Long parentChapterId);

	@Query("SELECT c FROM Chapter c WHERE c.parentChapterId is not null and c.member.id = :memberId")
	List<Chapter> findAllByParentChapterIdIsNotNullByMemberId(Long memberId);

	@Query("SELECT c FROM Chapter c WHERE c.member.id = :memberId and c.parentChapterId is not null")
	List<Chapter> findAllByMemberIdByParentChapterIdIsNotNullWithAutobiography(Long memberId);
}
