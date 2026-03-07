package com.lifelibrarians.lifebookshelf.member.service;

import com.lifelibrarians.lifebookshelf.exception.status.AuthExceptionStatus;
import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import com.lifelibrarians.lifebookshelf.member.domain.MemberMetadata;
import com.lifelibrarians.lifebookshelf.member.dto.request.MemberUpdateRequestDto;
import com.lifelibrarians.lifebookshelf.member.repository.MemberMetadataRepository;
import com.lifelibrarians.lifebookshelf.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
@Logging
public class MemberCommandService {

	private final MemberRepository memberRepository;
	private final MemberMetadataRepository memberMetadataRepository;

	public void updateMember(Long memberId, MemberUpdateRequestDto requestDto) {
		log.info("[UPDATE_MEMBER] 회원 정보 수정 시작 - memberId: {}, gender: {}, ageGroup: {}, occupation: {}", 
				memberId, requestDto.getGender(), requestDto.getAgeGroup(), requestDto.getOccupation());
		
		Member member = memberRepository.findByIdWithMetadata(memberId)
				.orElseThrow(AuthExceptionStatus.MEMBER_NOT_FOUND::toServiceException);

		if (member.getMemberMemberMetadata() == null) {
			log.info("[UPDATE_MEMBER] 신규 메타데이터 생성 - memberId: {}", memberId);
			
			MemberMetadata memberMetadata = MemberMetadata.ofV2(
					"",
					requestDto.getGender(),
					requestDto.getAgeGroup(),
					requestDto.getOccupation(),
					member
			);
			memberMetadata = memberMetadataRepository.save(memberMetadata);
			member.setMemberMemberMetadata(memberMetadata);
			
			log.info("[UPDATE_MEMBER] 메타데이터 생성 완료 - memberId: {}, metadataId: {}", memberId, memberMetadata.getId());
		} else {
			log.info("[UPDATE_MEMBER] 기존 메타데이터 수정 - memberId: {}, metadataId: {}", memberId, member.getMemberMemberMetadata().getId());
			
			member.getMemberMemberMetadata().updateV2(
					member.getMemberMemberMetadata().getName(),
					requestDto.getGender(),
					requestDto.getAgeGroup(),
					requestDto.getOccupation()
			);
			
			log.info("[UPDATE_MEMBER] 메타데이터 수정 완료 - memberId: {}", memberId);
		}
		
		log.info("[UPDATE_MEMBER] 회원 정보 수정 완료 - memberId: {}", memberId);
	}
}
