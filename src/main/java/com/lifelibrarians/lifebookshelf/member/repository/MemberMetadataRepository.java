package com.lifelibrarians.lifebookshelf.member.repository;

import com.lifelibrarians.lifebookshelf.member.domain.MemberMetadata;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface MemberMetadataRepository extends JpaRepository<MemberMetadata, Long> {

	@Query("SELECT mm FROM MemberMetadata mm JOIN FETCH mm.member WHERE mm.member.id = :memberId")
	Optional<MemberMetadata> findByMemberId(Long memberId);
}
