package com.lifelibrarians.lifebookshelf.auth.service;

import com.lifelibrarians.lifebookshelf.auth.dto.EmailLoginRequestDto;
import com.lifelibrarians.lifebookshelf.auth.dto.JwtLoginTokenDto;
import com.lifelibrarians.lifebookshelf.auth.jwt.JwtTokenProvider;
import com.lifelibrarians.lifebookshelf.exception.status.AuthExceptionStatus;
import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import com.lifelibrarians.lifebookshelf.member.domain.MemberRole;
import com.lifelibrarians.lifebookshelf.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Logging
public class AuthAdminService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // 관리자 전용 이메일 로그인
    public JwtLoginTokenDto adminEmailLogin(EmailLoginRequestDto requestDto) {
        // 1. 이메일로 회원 조회
        Optional<Member> member = memberRepository.findByEmail(requestDto.getEmail());

        // 1-1. 이미 탈퇴한 회원인 경우
        if (member.isPresent() && member.get().getDeletedAt() != null) {
            throw AuthExceptionStatus.MEMBER_ALREADY_WITHDRAWN.toServiceException();
        }

        // 2. 이메일 혹은 비밀번호가 틀린 경우
        if (member.isEmpty() || !member.get().getPasswordMember()
                .matchPassword(requestDto.getPassword())) {
            throw AuthExceptionStatus.EMAIL_OR_PASSWORD_INCORRECT.toServiceException();
        }

        // 3. 관리자 권한 체크 (추가된 부분)
        if (member.get().getRole() != MemberRole.ADMIN) {
            throw AuthExceptionStatus.MEMBER_IS_NOT_ADMIN.toServiceException();
        }

        // 4. 이미 탈퇴한 회원인 경우 (중복 체크이지만 기존 구조 유지)
        if (member.get().getDeletedAt() != null) {
            throw AuthExceptionStatus.MEMBER_ALREADY_WITHDRAWN.toServiceException();
        }

        // 6. JWT 토큰 생성
        Jwt jwt = jwtTokenProvider.createMemberAccessToken(member.get().getId());

        return JwtLoginTokenDto.builder()
                .accessToken(jwt.getTokenValue())
                .build();
    }
}
