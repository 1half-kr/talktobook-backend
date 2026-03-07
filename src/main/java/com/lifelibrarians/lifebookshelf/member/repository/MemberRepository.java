package com.lifelibrarians.lifebookshelf.member.repository;

import com.lifelibrarians.lifebookshelf.member.domain.LoginType;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import com.lifelibrarians.lifebookshelf.member.domain.MemberRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByEmail(String email);

	@Query("SELECT m FROM Member m LEFT OUTER JOIN FETCH m.memberMemberMetadata WHERE m.id = :memberId")
	Optional<Member> findByIdWithMetadata(Long memberId);

	@Query("SELECT m FROM Member m LEFT OUTER JOIN FETCH m.memberAutobiographies WHERE m.id = :memberId")
	Optional<Member> findMemberWithAutobiographiesByMemberId(Long memberId);

	// 관리자용 멤버 검색 및 필터링 쿼리 (관리자 제외, 메타데이터 포함)
	@Query(value = "SELECT m FROM Member m LEFT JOIN FETCH m.memberMemberMetadata " +
			"WHERE m.role IN ('PRE_MEMBER', 'MEMBER') " +
			"AND m.deletedAt IS NULL " +
			"AND (:emailSearch = '' OR LOWER(m.email) LIKE LOWER(CONCAT('%', :emailSearch, '%'))) " +
			"AND (:nicknameSearch = '' OR LOWER(m.nickname) LIKE LOWER(CONCAT('%', :nicknameSearch, '%'))) " +
			"AND (:nameSearch = '' OR LOWER(m.memberMemberMetadata.name) LIKE LOWER(CONCAT('%', :nameSearch, '%'))) " +
			"AND (:loginType IS NULL OR m.loginType = :loginType) " +
			"AND (:hasProfileImage IS NULL OR " +
			"     (:hasProfileImage = true AND m.profileImageUrl IS NOT NULL) OR " +
			"     (:hasProfileImage = false AND m.profileImageUrl IS NULL)) " +
			"AND (:createdAtStart IS NULL OR m.createdAt >= :createdAtStart) " +
			"AND (:createdAtEnd IS NULL OR m.createdAt <= :createdAtEnd)",
			countQuery = "SELECT COUNT(m) FROM Member m LEFT JOIN m.memberMemberMetadata " +
			"WHERE m.role IN ('PRE_MEMBER', 'MEMBER') " +
			"AND m.deletedAt IS NULL " +
			"AND (:emailSearch = '' OR LOWER(m.email) LIKE LOWER(CONCAT('%', :emailSearch, '%'))) " +
			"AND (:nicknameSearch = '' OR LOWER(m.nickname) LIKE LOWER(CONCAT('%', :nicknameSearch, '%'))) " +
			"AND (:nameSearch = '' OR LOWER(m.memberMemberMetadata.name) LIKE LOWER(CONCAT('%', :nameSearch, '%'))) " +
			"AND (:loginType IS NULL OR m.loginType = :loginType) " +
			"AND (:hasProfileImage IS NULL OR " +
			"     (:hasProfileImage = true AND m.profileImageUrl IS NOT NULL) OR " +
			"     (:hasProfileImage = false AND m.profileImageUrl IS NULL)) " +
			"AND (:createdAtStart IS NULL OR m.createdAt >= :createdAtStart) " +
			"AND (:createdAtEnd IS NULL OR m.createdAt <= :createdAtEnd)")
	Page<Member> findAllWithFilters(
			@Param("emailSearch") String emailSearch,
			@Param("nicknameSearch") String nicknameSearch,
			@Param("nameSearch") String nameSearch,
			@Param("loginType") LoginType loginType,
			@Param("hasProfileImage") Boolean hasProfileImage,
			@Param("createdAtStart") LocalDateTime createdAtStart,
			@Param("createdAtEnd") LocalDateTime createdAtEnd,
			Pageable pageable);

	// 일반 멤버 목록 조회 (관리자 제외, 메타데이터 포함)
	@Query(value = "SELECT m FROM Member m LEFT JOIN FETCH m.memberMemberMetadata " +
			"WHERE m.role IN ('PRE_MEMBER', 'MEMBER') " +
			"AND m.deletedAt IS NULL",
			countQuery = "SELECT COUNT(m) FROM Member m " +
			"WHERE m.role IN ('PRE_MEMBER', 'MEMBER') " +
			"AND m.deletedAt IS NULL")
	Page<Member> findAllNonAdminMembers(Pageable pageable);

	// 특정 역할의 멤버 상세 목록 조회 (관리자 제외)
	@Query(value = "SELECT m FROM Member m LEFT JOIN FETCH m.memberMemberMetadata " +
			"WHERE m.role IN ('PRE_MEMBER', 'MEMBER') " +
			"AND (:role IS NULL OR m.role = :role) " +
			"AND m.deletedAt IS NULL",
			countQuery = "SELECT COUNT(m) FROM Member m " +
			"WHERE m.role IN ('PRE_MEMBER', 'MEMBER') " +
			"AND (:role IS NULL OR m.role = :role) " +
			"AND m.deletedAt IS NULL")
	Page<Member> findDetailsByRole(@Param("role") MemberRole role, Pageable pageable);
}
