package com.lifelibrarians.lifebookshelf.community.book.repository;

import com.lifelibrarians.lifebookshelf.community.book.domain.BookContent;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BookContentRepository extends JpaRepository<BookContent, Long> {

}
