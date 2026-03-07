package com.lifelibrarians.lifebookshelf.community.book.repository;

import com.lifelibrarians.lifebookshelf.community.book.domain.BookChapter;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BookChapterRepository extends JpaRepository<BookChapter, Long> {

}
