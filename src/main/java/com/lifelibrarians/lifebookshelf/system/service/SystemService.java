package com.lifelibrarians.lifebookshelf.system.service;

import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.system.dto.request.FeedbackRequestDto;
import com.lifelibrarians.lifebookshelf.system.dto.response.TermsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Logging
public class SystemService {

	public TermsResponseDto getPrivacyPolicy() {
		// TODO: 실제 개인정보 처리방침 데이터 조회 로직 구현 필요
		return TermsResponseDto.builder()
				.content("개인정보 처리방침 내용")
				.version("1.0")
				.build();
	}

	public TermsResponseDto getServiceTerms() {
		// TODO: 실제 서비스 이용약관 데이터 조회 로직 구현 필요
		return TermsResponseDto.builder()
				.content("서비스 이용약관 내용")
				.version("1.0")
				.build();
	}

	public void submitFeedback(FeedbackRequestDto requestDto) {
		// TODO: 실제 피드백 저장 로직 구현 필요
		// 예: 데이터베이스에 저장, 이메일 전송 등
	}
}
