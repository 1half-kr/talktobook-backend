package com.lifelibrarians.lifebookshelf.auth.service;

import com.lifelibrarians.lifebookshelf.auth.domain.TemporaryUser;
import com.lifelibrarians.lifebookshelf.auth.dto.EmailLoginRequestDto;
import com.lifelibrarians.lifebookshelf.auth.dto.EmailRegisterRequestDto;
import com.lifelibrarians.lifebookshelf.auth.dto.JwtLoginTokenDto;
import com.lifelibrarians.lifebookshelf.auth.dto.ReIssueTokenRequestDto;
import com.lifelibrarians.lifebookshelf.exception.status.AuthExceptionStatus;
import com.lifelibrarians.lifebookshelf.auth.jwt.JwtTokenProvider;
import lombok.extern.log4j.Log4j2;
import com.lifelibrarians.lifebookshelf.member.domain.LoginType;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import com.lifelibrarians.lifebookshelf.member.domain.MemberRole;
import com.lifelibrarians.lifebookshelf.member.domain.PasswordMember;
import com.lifelibrarians.lifebookshelf.member.repository.MemberMetadataRepository;
import com.lifelibrarians.lifebookshelf.member.repository.MemberRepository;
import com.lifelibrarians.lifebookshelf.member.repository.PasswordMemberRepository;

import com.lifelibrarians.lifebookshelf.notification.service.NotificationCommandService;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class AuthService {

	private final JwtTokenProvider jwtTokenProvider;
	private final MemberRepository memberRepository;
	private final PasswordMemberRepository passwordMemberRepository;
	private final MemberMetadataRepository memberMetadataRepository;
	private final NotificationCommandService notificationService;
	private final EmailService emailService;
	private final TemporaryUserStore temporaryUserStore;
	
	@Qualifier("refreshTokenRedisTemplate")
	private final RedisTemplate<String, String> refreshTokenRedisTemplate;

	public void registerEmail(EmailRegisterRequestDto requestDto) {
		log.info("[REGISTER_EMAIL] 이메일 회원가입 시작 - email: {}", requestDto.getEmail());
		
		Optional<Member> optionalMember = memberRepository.findByEmail(requestDto.getEmail());
		if (optionalMember.isPresent()) {
			if (optionalMember.get().getDeletedAt() != null) {
				log.warn("[REGISTER_EMAIL] 이미 탈퇴한 회원 - email: {}", requestDto.getEmail());
				throw AuthExceptionStatus.MEMBER_ALREADY_WITHDRAWN.toServiceException();
			} else {
				log.warn("[REGISTER_EMAIL] 이미 존재하는 회원 - email: {}", requestDto.getEmail());
				throw AuthExceptionStatus.MEMBER_ALREADY_EXISTS.toServiceException();
			}
		}

		String code = generateVerificationCode();
        emailService.sendVerificationCode(requestDto.getEmail(), code);

		TemporaryUser temporaryUser = TemporaryUser.builder()
				.email(requestDto.getEmail())
				.password(requestDto.getPassword())
				.code(code)
				.expiresAt(LocalDateTime.now().plusMinutes(5))
				.build();
		temporaryUserStore.save(requestDto.getEmail(), temporaryUser);

        log.info("[REGISTER_EMAIL] 임시 사용자 저장 완료 - email: {}, code: {}", requestDto.getEmail(), code);
	}

	public void verifyEmail(String email, String code) {
        log.info("[VERIFY_EMAIL] 이메일 인증 시도 - email: {}, code: {}", email, code);

        Optional<TemporaryUser> optionalUser = temporaryUserStore.find(email);
        if (optionalUser.isEmpty()) {
            log.warn("[VERIFY_EMAIL] 인증 실패 - 해당 이메일에 대한 임시 사용자 없음 (email: {})", email);
            throw AuthExceptionStatus.NOT_FOUND_EMAIL.toServiceException();
        }

        TemporaryUser temporaryUser = optionalUser.get();
        log.info("[VERIFY_EMAIL] 임시 사용자 로드 성공 - user: {}", temporaryUser);

        if (!temporaryUser.matchCode(code)) {
            log.warn("[VERIFY_EMAIL] 인증 코드 불일치 - 입력: {}, 기대값: {}", code, temporaryUser.getCode());
            throw AuthExceptionStatus.INVALID_AUTH_CODE.toServiceException();
        }

        log.info("[VERIFY_EMAIL] 인증 코드 일치");

        Optional<Member> existingMember = memberRepository.findByEmail(email);
        if (existingMember.isPresent() && existingMember.get().getDeletedAt() != null) {
            // 탈퇴 회원 복구 (재가입)
            Member member = existingMember.get();
            member.restore();
            member.getPasswordMember().updatePassword(temporaryUser.getPassword());
            memberRepository.save(member);
            passwordMemberRepository.save(member.getPasswordMember());
            temporaryUserStore.remove(email);
            log.info("[VERIFY_EMAIL] 탈퇴 회원 재가입 완료 - memberId: {}", member.getId());
            return;
        }

        // 신규 회원 생성
        LocalDateTime now = LocalDateTime.now();
		PasswordMember passwordMember = PasswordMember.of(temporaryUser.getPassword());
		passwordMemberRepository.save(passwordMember);
		Member member = Member.of(
				LoginType.PASSWORD,
				temporaryUser.getEmail(),
				MemberRole.MEMBER,
				null,
				temporaryUser.getEmail().split("@")[0] + UUID.randomUUID().toString().substring(0, 6),
				now,
				now,
				null
		);
		member.addPasswordMember(passwordMember);
		memberRepository.save(member);
		temporaryUserStore.remove(email);
		log.info("[VERIFY_EMAIL] 신규 회원 가입 완료 - memberId: {}, email: {}", member.getId(), member.getEmail());
	}

	public void rejoinEmail(EmailRegisterRequestDto requestDto) {
		log.info("[REJOIN_EMAIL] 재가입 시작 - email: {}", requestDto.getEmail());

		Member member = memberRepository.findByEmail(requestDto.getEmail())
				.orElseThrow(AuthExceptionStatus.MEMBER_NOT_FOUND::toServiceException);

		if (member.getDeletedAt() == null) {
			log.warn("[REJOIN_EMAIL] 탈퇴하지 않은 회원 - email: {}", requestDto.getEmail());
			throw AuthExceptionStatus.MEMBER_ALREADY_EXISTS.toServiceException();
		}

		String code = generateVerificationCode();
		emailService.sendVerificationCode(requestDto.getEmail(), code);

		TemporaryUser temporaryUser = TemporaryUser.builder()
				.email(requestDto.getEmail())
				.password(requestDto.getPassword())
				.code(code)
				.expiresAt(LocalDateTime.now().plusMinutes(5))
				.build();
		temporaryUserStore.save(requestDto.getEmail(), temporaryUser);

		log.info("[REJOIN_EMAIL] 재가입 인증 코드 발송 완료 - email: {}, code: {}", requestDto.getEmail(), code);
	}

	public void resendVerificationCode(String email) {
		log.info("[RESEND_CODE] 인증 코드 재발급 시작 - email: {}", email);

		Optional<TemporaryUser> optionalUser = temporaryUserStore.find(email);
		if (optionalUser.isEmpty()) {
			log.warn("[RESEND_CODE] 임시 사용자 없음 - email: {}", email);
			throw AuthExceptionStatus.NOT_FOUND_EMAIL.toServiceException();
		}

		TemporaryUser temporaryUser = optionalUser.get();
		String newCode = generateVerificationCode();
		emailService.sendVerificationCode(email, newCode);

		TemporaryUser updatedUser = TemporaryUser.builder()
				.email(temporaryUser.getEmail())
				.password(temporaryUser.getPassword())
				.code(newCode)
				.expiresAt(LocalDateTime.now().plusMinutes(5))
				.build();
		temporaryUserStore.save(email, updatedUser);

		log.info("[RESEND_CODE] 인증 코드 재발급 완료 - email: {}, code: {}", email, newCode);
	}

    public JwtLoginTokenDto loginEmail(EmailLoginRequestDto requestDto) {
        log.info("[LOGIN_EMAIL] 이메일 로그인 시작 - email: {}", requestDto.getEmail());

        Optional<Member> member = memberRepository.findByEmail(requestDto.getEmail());

        if (member.isPresent() && member.get().getDeletedAt() != null) {
            log.warn("[LOGIN_EMAIL] 탈퇴한 회원 - email: {}", requestDto.getEmail());
            throw AuthExceptionStatus.MEMBER_ALREADY_WITHDRAWN.toServiceException();
        }

        if (member.isEmpty() || !member.get().getPasswordMember()
                .matchPassword(requestDto.getPassword())) {
            log.warn("[LOGIN_EMAIL] 이메일 또는 비밀번호 불일치 - email: {}", requestDto.getEmail());
            throw AuthExceptionStatus.EMAIL_OR_PASSWORD_INCORRECT.toServiceException();
        }

        if (member.get().getRole() == MemberRole.PRE_MEMBER) {
            log.warn("[LOGIN_EMAIL] 이메일 미인증 회원 - memberId: {}", member.get().getId());
            throw AuthExceptionStatus.EMAIL_NOT_VERIFIED.toServiceException();
        }

        Jwt accessToken = jwtTokenProvider.createMemberAccessToken(member.get().getId());
        Jwt refreshToken = jwtTokenProvider.createMemberRefreshToken(member.get().getId());
        log.info("[LOGIN_EMAIL] JWT 토큰 발급 완료 - memberId: {}", member.get().getId());

        // Redis에 Access Token과 Refresh Token 저장
        String memberId = member.get().getId().toString();
        refreshTokenRedisTemplate.opsForValue().set("refresh:" + memberId, refreshToken.getTokenValue(), Duration.ofDays(30));
        refreshTokenRedisTemplate.opsForValue().set("access:" + memberId, accessToken.getTokenValue(), Duration.ofDays(7));
        log.info("[LOGIN_EMAIL] Redis 토큰 저장 완료 - memberId: {}", memberId);

        // Device Token 업데이트
        if (requestDto.getDeviceToken() != null && !requestDto.getDeviceToken().isEmpty()) {
            log.info("[LOGIN_EMAIL] Device Token 업데이트 시작 - memberId: {}", memberId);
            notificationService.updateDeviceToken(member.get(), requestDto.getDeviceToken(),
                    LocalDateTime.now());
        }

        // 4) metadata 입력 여부 확인
        boolean metadataSuccessed = memberMetadataRepository.findByMemberId(member.get().getId())
                .map(metadata -> metadata.getGender() != null
                        && metadata.getOccupation() != null
                        && metadata.getAgeGroup() != null)
                .orElse(false);
        log.info("[LOGIN_EMAIL] 로그인 완료 - memberId: {}, metadataSuccessed: {}", memberId, metadataSuccessed);

        // 5) 반환
        return JwtLoginTokenDto.builder()
                .accessToken(accessToken.getTokenValue())
                .refreshToken(refreshToken.getTokenValue())
                .metadataSuccessed(metadataSuccessed)
                .build();
    }

    // 토큰 재발급
    public JwtLoginTokenDto reissueToken(ReIssueTokenRequestDto requestDto) {
        log.info("[REISSUE_TOKEN] 토큰 재발급 시작");
        
        // JWT 파싱 + 서명 검증
        Jwt refreshToken = jwtTokenProvider.parseToken(requestDto.getRefreshToken());
        log.info("[REISSUE_TOKEN] Refresh Token 파싱 완료");

        // 만료 확인
        jwtTokenProvider.validateRefreshToken(refreshToken);
        log.info("[REISSUE_TOKEN] Refresh Token 유효성 검증 완료");

        // 토큰에서 memberId 추출
        Long memberId = jwtTokenProvider.extractMemberIdFromRefreshToken(refreshToken);
        log.info("[REISSUE_TOKEN] memberId 추출 완료 - memberId: {}", memberId);

        // Redis에서 저장된 Refresh Token 확인
        String refreshKey = "refresh:" + memberId;
        String storedRefreshToken = refreshTokenRedisTemplate.opsForValue().get(refreshKey);
        
        if (storedRefreshToken == null || !storedRefreshToken.equals(requestDto.getRefreshToken())) {
            log.warn("[REISSUE_TOKEN] Redis의 Refresh Token과 불일치 - memberId: {}", memberId);
            throw AuthExceptionStatus.INVALID_REFRESH_TOKEN.toServiceException();
        }
        log.info("[REISSUE_TOKEN] Redis Refresh Token 검증 완료 - memberId: {}", memberId);

        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(AuthExceptionStatus.MEMBER_NOT_FOUND::toServiceException);

        if (member.getDeletedAt() != null) {
            log.warn("[REISSUE_TOKEN] 탈퇴한 회원 - memberId: {}", memberId);
            throw AuthExceptionStatus.MEMBER_ALREADY_WITHDRAWN.toServiceException();
        }

        // 새 토큰 발급 (Rotation)
        log.info("[REISSUE_TOKEN] 새 토큰 발급 시작 - memberId: {}", memberId);
        Jwt newAccess  = jwtTokenProvider.createMemberAccessToken(memberId);
        Jwt newRefresh = jwtTokenProvider.createMemberRefreshToken(memberId);

        // Redis에 새 토큰들 저장 (기존 토큰 무효화)
        String memberIdStr = memberId.toString();
        refreshTokenRedisTemplate.opsForValue().set("refresh:" + memberIdStr, newRefresh.getTokenValue(), Duration.ofDays(30));
        refreshTokenRedisTemplate.opsForValue().set("access:" + memberIdStr, newAccess.getTokenValue(), Duration.ofDays(7));
        log.info("[REISSUE_TOKEN] 토큰 재발급 완료 - memberId: {}", memberId);

        return JwtLoginTokenDto.builder()
                .accessToken(newAccess.getTokenValue())
                .refreshToken(newRefresh.getTokenValue())
                .build();
    }

	public void resetPassword(String email) {
		log.info("[RESET_PASSWORD] 비밀번호 재설정 시작 - email: {}", email);
		
		Member member = memberRepository.findByEmail(email)
				.orElseThrow(AuthExceptionStatus.MEMBER_NOT_FOUND::toServiceException);

		if (member.getDeletedAt() != null) {
			log.warn("[RESET_PASSWORD] 탈퇴한 회원 - memberId: {}", member.getId());
			throw AuthExceptionStatus.MEMBER_ALREADY_WITHDRAWN.toServiceException();
		}

		String tempPassword = generateTemporaryPassword();
		member.getPasswordMember().updatePassword(tempPassword);
		passwordMemberRepository.save(member.getPasswordMember());
		log.info("[RESET_PASSWORD] 임시 비밀번호 생성 및 저장 완료 - memberId: {}", member.getId());
		
		emailService.sendTemporaryPassword(email, tempPassword);
		log.info("[RESET_PASSWORD] 임시 비밀번호 이메일 전송 완료 - email: {}", email);
	}

    public void logout(Long memberId) {
        log.info("[LOGOUT] 로그아웃 시작 - memberId: {}", memberId);
        
        Member member = memberRepository.findById(memberId)
                .orElseThrow(AuthExceptionStatus.MEMBER_NOT_FOUND::toServiceException);
        if (member.getDeletedAt() != null) {
            log.warn("[LOGOUT] 탈퇴한 회원 - memberId: {}", memberId);
            throw AuthExceptionStatus.MEMBER_ALREADY_WITHDRAWN.toServiceException();
        }
        
        // Redis에서 Access Token과 Refresh Token 모두 삭제
        String memberIdStr = memberId.toString();
        refreshTokenRedisTemplate.delete("access:" + memberIdStr);
        refreshTokenRedisTemplate.delete("refresh:" + memberIdStr);
        log.info("[LOGOUT] 로그아웃 완료 - memberId: {}", memberId);
    }

	public void unregister(Long memberId) {
		log.info("[UNREGISTER] 회원 탈퇴 시작 - memberId: {}", memberId);
		
		Member member = memberRepository.findById(memberId)
				.orElseThrow(AuthExceptionStatus.MEMBER_NOT_FOUND::toServiceException);
		LocalDateTime now = LocalDateTime.now();
		member.softDelete(now);
		memberRepository.save(member);
		log.info("[UNREGISTER] 회원 탈퇴 완료 - memberId: {}, deletedAt: {}", memberId, now);
	}

	private String generateVerificationCode() {
		return String.format("%06d", new Random().nextInt(1000000));
	}

	private String generateTemporaryPassword() {
		return UUID.randomUUID().toString().substring(0, 12);
	}
}
