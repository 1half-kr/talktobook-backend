package com.lifelibrarians.lifebookshelf.autobiography.repository;

import com.lifelibrarians.lifebookshelf.autobiography.domain.Autobiography;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatusType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface AutobiographyRepository extends JpaRepository<Autobiography, Long> {

	@Query("SELECT a FROM Autobiography a JOIN FETCH a.chapter WHERE a.member.id = :memberId")
	List<Autobiography> findByMemberId(Long memberId);

	@Query("SELECT a FROM Autobiography a JOIN FETCH a.autobiographyInterviews WHERE a.id = :autobiographyId")
	Optional<Autobiography> findWithInterviewById(Long autobiographyId);

	@Query("SELECT a FROM Autobiography a JOIN FETCH a.autobiographyInterviews WHERE a.member.id = :memberId")
	List<Autobiography> findWithInterviewByMemberId(Long memberId);

    // autobiography status를 여러 개 선택해서 필터링하여 페이지네이션으로 조회
    @Query(
            value = "SELECT a FROM Autobiography a JOIN a.autobiographyStatus s " +
                    "WHERE a.member.id = :memberId " +
                    "AND s.status IN :statuses",
            countQuery = "SELECT COUNT(a) FROM Autobiography a JOIN a.autobiographyStatus s " +
                    "WHERE a.member.id = :memberId " +
                    "AND s.status IN :statuses"
    )
    Page<Autobiography> findByMemberIdAndStatusesPaged(@Param("memberId") Long memberId,
                                                       @Param("statuses") List<AutobiographyStatusType> statuses,
                                                       Pageable pageable);

    // 관리자용 검색 및 필터링 쿼리
	@Query("SELECT a FROM Autobiography a " +
			"WHERE (:search = '' OR LOWER(a.title) LIKE LOWER(CONCAT('%', :search, '%'))) " +
			"AND (:hasCoverImage IS NULL OR " +
			"     (:hasCoverImage = true AND a.coverImageUrl IS NOT NULL) OR " +
			"     (:hasCoverImage = false AND a.coverImageUrl IS NULL)) " +
			"AND (:memberId = 0 OR a.member.id = :memberId) " +
			"AND (:createdAtStart IS NULL OR a.createdAt >= :createdAtStart) " +
			"AND (:createdAtEnd IS NULL OR a.createdAt <= :createdAtEnd) " +
			"AND (:updatedAtStart IS NULL OR a.updatedAt >= :updatedAtStart) " +
			"AND (:updatedAtEnd IS NULL OR a.updatedAt <= :updatedAtEnd)")
	Page<Autobiography> findAllWithFilters(
			@Param("search") String search,
			@Param("hasCoverImage") Boolean hasCoverImage,
			@Param("memberId") Integer memberId,
			@Param("createdAtStart") LocalDateTime createdAtStart,
			@Param("createdAtEnd") LocalDateTime createdAtEnd,
			@Param("updatedAtStart") LocalDateTime updatedAtStart,
			@Param("updatedAtEnd") LocalDateTime updatedAtEnd,
			Pageable pageable);

	// 특정 멤버의 자서전 상세 목록 조회 (통합본 빠른 검색용) - 정렬 지원
	@Query(value = "SELECT a FROM Autobiography a JOIN FETCH a.chapter JOIN FETCH a.member " +
			"WHERE a.member.id = :memberId",
			countQuery = "SELECT COUNT(a) FROM Autobiography a WHERE a.member.id = :memberId")
	Page<Autobiography> findDetailsByMemberId(@Param("memberId") Long memberId, Pageable pageable);
}
