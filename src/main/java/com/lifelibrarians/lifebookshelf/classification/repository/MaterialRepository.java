package com.lifelibrarians.lifebookshelf.classification.repository;

import com.lifelibrarians.lifebookshelf.classification.domain.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MaterialRepository extends JpaRepository<Material, Long> {

    @Query("SELECT m FROM Material m " +
            "JOIN m.chunk ch " +
            "JOIN ch.category c " +
            "JOIN c.themes t " +
            "WHERE c.autobiography.id = :autobiographyId " +
            "AND t.order = :themeOrder " +
            "AND c.order = :categoryOrder " +
            "AND ch.order = :chunkOrder " +
            "AND m.order = :materialOrder")
    Optional<Material> findByAutobiographyAndThemeAndOrdersAndMaterialOrder(@Param("autobiographyId") Long autobiographyId, @Param("themeOrder") Integer themeOrder, @Param("categoryOrder") Integer categoryOrder, @Param("chunkOrder") Integer chunkOrder, @Param("materialOrder") Integer materialOrder);

    @Query("SELECT m FROM Material m " +
            "JOIN FETCH m.chunk ch " +
            "JOIN FETCH ch.category c " +
            "WHERE c.autobiography.id = :autobiographyId " +
            "ORDER BY m.count DESC")
    List<Material> findAllByAutobiographyIdOrderByCountDesc(@Param("autobiographyId") Long autobiographyId);

    @Query("SELECT COUNT(m) " +
           "FROM Material m " +
           "JOIN m.chunk ch " +
           "JOIN ch.category c " +
           "JOIN c.autobiography a " +
           "JOIN Theme t ON LOWER(a.theme) = LOWER(t.name) " +
           "JOIN t.categories tc " +
           "WHERE a.member.id = :memberId " +
           "AND a.theme = :theme " +
           "AND tc.id = c.id")
    Long countMaterialsByAutobiographyTheme(@Param("memberId") Long memberId, @Param("theme") String theme);

    @Query("SELECT COUNT(m) " +
           "FROM Material m " +
           "JOIN m.chunk ch " +
           "JOIN ch.category c " +
           "JOIN c.autobiography a " +
           "JOIN Theme t ON LOWER(a.theme) = LOWER(t.name) " +
           "JOIN t.categories tc " +
           "WHERE a.member.id = :memberId " +
           "AND a.theme = :theme " +
           "AND tc.id = c.id " +
           "AND m.count = 1")
    Long countCompletedMaterialsByAutobiographyTheme(@Param("memberId") Long memberId, @Param("theme") String theme);

    @Query("SELECT c.name " +
           "FROM Category c " +
           "JOIN c.autobiography a " +
           "JOIN Theme t ON LOWER(a.theme) = LOWER(t.name) " +
           "JOIN t.categories tc " +
           "WHERE a.member.id = :memberId " +
           "AND a.theme = :theme " +
           "AND tc.id = c.id " +
           "AND c.order = :categoryOrder")
    Optional<String> findCategoryNameByAutobiographyAndOrder(@Param("memberId") Long memberId, 
                                                           @Param("theme") String theme, 
                                                           @Param("categoryOrder") Integer categoryOrder);
}
