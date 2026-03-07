package com.lifelibrarians.lifebookshelf.auth.controller;

import com.lifelibrarians.lifebookshelf.auth.dto.*;
import com.lifelibrarians.lifebookshelf.exception.status.AuthExceptionStatus;
import com.lifelibrarians.lifebookshelf.auth.jwt.LoginMemberInfo;
import com.lifelibrarians.lifebookshelf.auth.password.annotation.OneWayEncryption;
import com.lifelibrarians.lifebookshelf.auth.password.annotation.TargetMapping;
import com.lifelibrarians.lifebookshelf.auth.service.AuthService;
import com.lifelibrarians.lifebookshelf.exception.annotation.ApiErrorCodeExample;
import com.lifelibrarians.lifebookshelf.log.Logging;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v2/auth")
@Tag(name = "인증 (Auth)", description = "인증 관련 API")
@Logging
public class AuthController {

    private final AuthService authService;


    @Operation(summary = "이메일 회원가입", description = "이메일 회원가입을 요청합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "accepted"),
    })
    @ApiErrorCodeExample(
            authExceptionStatuses = {
                    AuthExceptionStatus.INVALID_EMAIL_FORMAT,
                    AuthExceptionStatus.EMAIL_TOO_LONG,
                    AuthExceptionStatus.PASSWORD_FORMAT_ERROR,
                    AuthExceptionStatus.MEMBER_ALREADY_EXISTS,
                    AuthExceptionStatus.MEMBER_ALREADY_WITHDRAWN
            }
    )
    @PostMapping(value = "/email-register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @OneWayEncryption({
            @TargetMapping(clazz = EmailRegisterRequestDto.class, fields = {
                    EmailRegisterRequestDto.Fields.password})
    })
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void registerEmail(
            @Valid @ModelAttribute EmailRegisterRequestDto requestDto
    ) {
        authService.registerEmail(requestDto);
    }

    @Operation(summary = "이메일 인증 요청", description = "인증코드로 이메일 인증을 요청합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "created")
    })
    @ApiErrorCodeExample(
            authExceptionStatuses = {
                    AuthExceptionStatus.INVALID_EMAIL_FORMAT,
                    AuthExceptionStatus.EMAIL_TOO_LONG,
                    AuthExceptionStatus.INVALID_AUTH_CODE
            }
    )
    @PostMapping(value = "/email-verify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void verifyEmail(
            @Valid @ModelAttribute VerifyEmailRequestDto requestDto
    ) {
        authService.verifyEmail(requestDto.getEmail(), requestDto.getVerificationCode());
    }

    @Operation(summary = "이메일 로그인", description = "이메일 로그인을 요청합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ok")
    })
    @ApiErrorCodeExample(
            authExceptionStatuses = {
                    AuthExceptionStatus.INVALID_EMAIL_FORMAT,
                    AuthExceptionStatus.EMAIL_TOO_LONG,
                    AuthExceptionStatus.PASSWORD_FORMAT_ERROR,
                    AuthExceptionStatus.EMAIL_OR_PASSWORD_INCORRECT,
                    AuthExceptionStatus.EMAIL_NOT_VERIFIED,
                    AuthExceptionStatus.MEMBER_NOT_FOUND,
                    AuthExceptionStatus.MEMBER_ALREADY_WITHDRAWN
            }
    )
    @PostMapping(value = "/email-login", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @OneWayEncryption({
            @TargetMapping(clazz = EmailLoginRequestDto.class, fields = {
                    EmailLoginRequestDto.Fields.password})
    })
    public JwtLoginTokenDto loginEmail(
            @Valid @ModelAttribute EmailLoginRequestDto requestDto
    ) {
        return authService.loginEmail(requestDto);
    }

    @Operation(summary = "비밀번호 초기화 요청", description = "비밀번호 초기화를 요청합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ok")
    })
    @ApiErrorCodeExample(
            authExceptionStatuses = {
                    AuthExceptionStatus.INVALID_EMAIL_FORMAT,
                    AuthExceptionStatus.EMAIL_TOO_LONG,
                    AuthExceptionStatus.MEMBER_NOT_FOUND
            }
    )
    @PostMapping(value = "/reset-password", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void resetPassword(
            @Valid @ModelAttribute PasswordResetRequestDto requestDto
    ) {
        authService.resetPassword(requestDto.getEmail());
    }

    @Operation(summary = "access token 재발급", description = "refresh 토큰으로 access token, refresh token을 재발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ok")
    })
    @ApiErrorCodeExample(
            authExceptionStatuses = {
                    AuthExceptionStatus.INVALID_REFRESH_TOKEN,
                    AuthExceptionStatus.REFRESH_TOKEN_EXPIRED,
                    AuthExceptionStatus.MEMBER_NOT_FOUND,
                    AuthExceptionStatus.MEMBER_ALREADY_WITHDRAWN
            }
    )
    @PostMapping(value = "/reissue", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public JwtLoginTokenDto reissueToken(
            @Valid @ModelAttribute ReIssueTokenRequestDto requestDto
    ) {
        return authService.reissueToken(requestDto);
    }

    @Operation(summary = "로그아웃", description = "로그아웃으로 token 인증을 차단 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "no contents")
    })
    @ApiErrorCodeExample(
            authExceptionStatuses = {
                    AuthExceptionStatus.MEMBER_NOT_FOUND,
                    AuthExceptionStatus.MEMBER_ALREADY_WITHDRAWN
            }
    )
    @PostMapping(value = "/logout")
    public void logout(
            @LoginMemberInfo MemberSessionDto memberSessionDto
    ) {
        authService.logout(memberSessionDto.getMemberId());
    }

    @Operation(summary = "회원탈퇴 요청", description = "회원탈퇴를 요청합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "no content")
    })
    @ApiErrorCodeExample(
            authExceptionStatuses = {
                    AuthExceptionStatus.MEMBER_NOT_FOUND,
                    AuthExceptionStatus.MEMBER_ALREADY_WITHDRAWN
            }
    )
    @DeleteMapping(value = "/unregister")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    public void unregister(
            @LoginMemberInfo MemberSessionDto memberSessionDto
    ) {
        authService.unregister(memberSessionDto.getMemberId());
    }

    @Operation(summary = "이메일 재가입", description = "탈퇴한 회원이 재가입을 요청합니다. 인증번호를 발송합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "accepted")
    })
    @ApiErrorCodeExample(
            authExceptionStatuses = {
                    AuthExceptionStatus.INVALID_EMAIL_FORMAT,
                    AuthExceptionStatus.EMAIL_TOO_LONG,
                    AuthExceptionStatus.PASSWORD_FORMAT_ERROR,
                    AuthExceptionStatus.MEMBER_NOT_FOUND,
                    AuthExceptionStatus.MEMBER_ALREADY_EXISTS
            }
    )
    @PostMapping(value = "/email-rejoin", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @OneWayEncryption({
            @TargetMapping(clazz = EmailRegisterRequestDto.class, fields = {
                    EmailRegisterRequestDto.Fields.password})
    })
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void rejoinEmail(
            @Valid @ModelAttribute EmailRegisterRequestDto requestDto
    ) {
        authService.rejoinEmail(requestDto);
    }

    @Operation(summary = "인증 코드 재발급", description = "회원가입/재가입 시 인증 코드를 재발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ok")
    })
    @ApiErrorCodeExample(
            authExceptionStatuses = {
                    AuthExceptionStatus.INVALID_EMAIL_FORMAT,
                    AuthExceptionStatus.EMAIL_TOO_LONG,
                    AuthExceptionStatus.NOT_FOUND_EMAIL
            }
    )
    @PostMapping(value = "/resend-code", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void resendVerificationCode(
            @Valid @ModelAttribute ResendCodeRequestDto requestDto
    ) {
        authService.resendVerificationCode(requestDto.getEmail());
    }
}
