package com.lifelibrarians.lifebookshelf.community.comment.repository;

import com.lifelibrarians.lifebookshelf.community.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CommentRepository extends JpaRepository<Comment, Long> {

}
