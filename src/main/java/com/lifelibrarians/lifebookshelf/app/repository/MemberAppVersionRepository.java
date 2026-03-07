package com.lifelibrarians.lifebookshelf.app.repository;

import com.lifelibrarians.lifebookshelf.app.domain.MemberAppVersions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberAppVersionRepository extends JpaRepository<MemberAppVersions, Long>  {
    // memberId로 MemberAppVersions 엔티티를 찾는 메서드
    Optional<MemberAppVersions> findByMemberId(Long memberId);
}
