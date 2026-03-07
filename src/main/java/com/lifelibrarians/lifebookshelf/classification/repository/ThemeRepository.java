package com.lifelibrarians.lifebookshelf.classification.repository;

import com.lifelibrarians.lifebookshelf.classification.domain.Theme;
import com.lifelibrarians.lifebookshelf.classification.domain.ThemeNameType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ThemeRepository extends JpaRepository<Theme, Long> {
    
    @Query(value = "SELECT DISTINCT t.* FROM theme t INNER JOIN theme_category tc ON t.id = tc.theme_id WHERE t.name = :name LIMIT 1", nativeQuery = true)
    Optional<Theme> findOneByName(@Param("name") String name);
}
