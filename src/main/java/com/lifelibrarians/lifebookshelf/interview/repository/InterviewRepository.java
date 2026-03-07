package com.lifelibrarians.lifebookshelf.interview.repository;

import com.lifelibrarians.lifebookshelf.interview.domain.Interview;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InterviewRepository extends JpaRepository<Interview, Long> {

	@Query("SELECT i FROM Interview i "
			+ "JOIN FETCH i.questions "
			+ "JOIN FETCH i.currentQuestion "
			+ "WHERE i.id = :interviewId")
	Optional<Interview> findWithQuestionsById(Long interviewId);

	@Query("SELECT i FROM Interview i "
			+ "LEFT JOIN FETCH i.questions "
			+ "LEFT JOIN FETCH i.currentQuestion "
			+ "WHERE i.id = :interviewId")
	Optional<Interview> findWithQuestionsAndCurrentQuestionById(Long interviewId);

	// 특정 멤버의 인터뷰 개수 조회
	@Query("SELECT COUNT(i) FROM Interview i WHERE i.member.id = :memberId")
	Long countByMemberId(@Param("memberId") Long memberId);

    Optional<Interview> findTopByAutobiographyIdOrderByCreatedAtDesc(Long autobiographyId);

    // 특정 auto id의 인터뷰 중 특정 년, 월의 모든 인터뷰 조회
    @Query("SELECT i FROM Interview i WHERE i.autobiography.id = :autobiographyId "
            + "AND FUNCTION('YEAR', i.createdAt) = :year "
            + "AND FUNCTION('MONTH', i.createdAt) = :month")
    List<Interview> findAllByAutobiographyIdAndYearAndMonth(@Param("autobiographyId") Long autobiographyId,
                                                            @Param("year") Integer year,
                                                            @Param("month") Integer month);
}
