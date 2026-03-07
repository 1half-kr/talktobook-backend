package com.lifelibrarians.lifebookshelf.autobiography.repository;

import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatus;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatusType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AutobiographyStatusRepository extends JpaRepository<AutobiographyStatus, Long> {
    // member id로 상태 조회
    List<AutobiographyStatus> findByMemberId(Long memberId);

    // type이 EMPTY이거나 PROGRESSING인 것 중 가장 최근에 업데이트된 상태 조회
    Optional<AutobiographyStatus> findTopByMemberIdAndStatusInOrderByUpdatedAtDesc(Long memberId, List<AutobiographyStatusType> statuses);

}
