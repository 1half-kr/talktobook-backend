package com.lifelibrarians.lifebookshelf.classification.repository;

import com.lifelibrarians.lifebookshelf.classification.domain.Chunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChunkRepository extends JpaRepository<Chunk, Long> {
    
    @Query("SELECT c FROM Chunk c " +
           "JOIN c.category cat " +
           "JOIN cat.themes t " +
           "WHERE cat.autobiography.id = :autobiographyId " +
           "AND t.order = :themeOrder " +
           "AND cat.order = :categoryOrder " +
           "AND c.order = :chunkOrder")
    Optional<Chunk> findByAutobiographyAndThemeAndCategoryOrderAndChunkOrder(@Param("autobiographyId") Long autobiographyId, @Param("themeOrder") Integer themeOrder, @Param("categoryOrder") Integer categoryOrder, @Param("chunkOrder") Integer chunkOrder);
}
