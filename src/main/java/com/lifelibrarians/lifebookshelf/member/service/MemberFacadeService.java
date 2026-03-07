package com.lifelibrarians.lifebookshelf.member.service;

import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.member.dto.request.MemberUpdateRequestDto;
import com.lifelibrarians.lifebookshelf.member.dto.response.MemberBasicResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Logging
public class MemberFacadeService {

	private final MemberQueryService memberQueryService;
	private final MemberCommandService memberCommandService;

	public MemberBasicResponseDto getMember(Long memberId) {
		return memberQueryService.getMember(memberId);
	}

	public void updateMember(Long memberId, MemberUpdateRequestDto requestDto) {
		memberCommandService.updateMember(memberId, requestDto);
	}
}
