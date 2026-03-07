package com.lifelibrarians.lifebookshelf.interview.repository;

import com.lifelibrarians.lifebookshelf.interview.domain.Interview;
import com.lifelibrarians.lifebookshelf.interview.domain.InterviewQuestion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface InterviewQuestionRepository extends JpaRepository<InterviewQuestion, String> {

	@Query("SELECT iq FROM InterviewQuestion iq WHERE iq.interview.id = :interviewId")
	List<InterviewQuestion> findAllByInterviewId(Long interviewId);

    // interviewId와 questionOrder로 InterviewQuestion 찾기
    @Query("SELECT iq FROM InterviewQuestion iq WHERE iq.interview.id = :interviewId AND iq.questionOrder = :questionOrder")
    Optional<InterviewQuestion> findByInterviewIdAndQuestionOrder(Long interviewId, Integer questionOrder);
}
