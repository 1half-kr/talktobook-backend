package com.lifelibrarians.lifebookshelf.member.service;

import com.lifelibrarians.lifebookshelf.exception.status.MemberExceptionStatus;
import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.member.domain.MemberMetadata;
import com.lifelibrarians.lifebookshelf.member.dto.response.MemberBasicResponseDto;
import com.lifelibrarians.lifebookshelf.member.repository.MemberMetadataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Logging
public class MemberQueryService {

	private final MemberMetadataRepository memberMetadataRepository;

	public MemberBasicResponseDto getMember(Long memberId) {
		log.info("[GET_MEMBER] 회원 정보 조회 시작 - memberId: {}", memberId);
		
		MemberMetadata memberMetadata = memberMetadataRepository.findByMemberId(memberId)
				.orElseThrow(MemberExceptionStatus.MEMBER_METADATA_NOT_FOUND::toServiceException);

		boolean isSuccessed = memberMetadata.getGender() != null 
				&& memberMetadata.getOccupation() != null 
				&& memberMetadata.getAgeGroup() != null;

		log.info("[GET_MEMBER] 회원 정보 조회 완료 - memberId: {}, metadataId: {}, successed: {}", 
				memberId, memberMetadata.getId(), isSuccessed);

		return MemberBasicResponseDto.builder()
				.gender(memberMetadata.getGender())
				.occupation(memberMetadata.getOccupation())
				.ageGroup(memberMetadata.getAgeGroup())
				.successed(isSuccessed)
				.build();
	}
}
