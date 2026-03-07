package com.lifelibrarians.lifebookshelf.classification.repository;

import com.lifelibrarians.lifebookshelf.classification.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c.name FROM Category c WHERE c.autobiography.id = :autobiographyId AND c.order = :categoryOrder")
    Optional<String> findNameByOrder(
            @Param("autobiographyId") Long autobiographyId,
            @Param("categoryOrder") Integer categoryOrder
    );

    @Query("SELECT c.name FROM Category c WHERE c.order = :categoryOrder ORDER BY c.id ASC")
    List<String> findAnyNameByOrder(@Param("categoryOrder") Integer categoryOrder);

}
