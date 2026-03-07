package com.lifelibrarians.lifebookshelf.chapter.repository;

import com.lifelibrarians.lifebookshelf.chapter.domain.ChapterStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface ChapterStatusRepository extends JpaRepository<ChapterStatus, Long> {

	@Query("SELECT cs FROM ChapterStatus cs WHERE cs.member.id = :memberId")
	Optional<ChapterStatus> findByMemberId(Long memberId);
}
