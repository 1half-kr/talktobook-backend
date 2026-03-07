package com.lifelibrarians.lifebookshelf.community.book.repository;

import com.lifelibrarians.lifebookshelf.community.book.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BookRepository extends JpaRepository<Book, Long> {

}
