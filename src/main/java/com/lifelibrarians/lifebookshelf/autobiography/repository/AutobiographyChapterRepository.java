package com.lifelibrarians.lifebookshelf.autobiography.repository;

import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyChapter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AutobiographyChapterRepository extends JpaRepository<AutobiographyChapter, Long> {
    List<AutobiographyChapter> findByAutobiographyIdOrderByCreatedAtAsc(Long autobiographyId);
}
