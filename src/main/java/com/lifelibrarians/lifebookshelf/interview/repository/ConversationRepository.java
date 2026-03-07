package com.lifelibrarians.lifebookshelf.interview.repository;

import com.lifelibrarians.lifebookshelf.interview.domain.Conversation;
import com.lifelibrarians.lifebookshelf.interview.domain.ConversationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

	@Query("select c from Conversation c where c.interview.id = :interviewId")
	Page<Conversation> findAllByInterviewId(Long interviewId, Pageable pageable);
	
	@Query("select c from Conversation c where c.interview.autobiography.member.id = :memberId and c.conversationType = :conversationType")
	List<Conversation> findByMemberIdAndConversationType(Long memberId, ConversationType conversationType);

	@Query("select c from Conversation c where c.interview.autobiography.id = :autobiographyId")
	List<Conversation> findByAutobiographyId(Long autobiographyId);
}
