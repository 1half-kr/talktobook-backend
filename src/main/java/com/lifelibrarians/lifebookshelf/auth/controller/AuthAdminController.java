package com.lifelibrarians.lifebookshelf.auth.controller;

import com.lifelibrarians.lifebookshelf.auth.dto.EmailLoginRequestDto;
import com.lifelibrarians.lifebookshelf.auth.dto.JwtLoginTokenDto;
import com.lifelibrarians.lifebookshelf.auth.password.annotation.OneWayEncryption;
import com.lifelibrarians.lifebookshelf.auth.password.annotation.TargetMapping;
import com.lifelibrarians.lifebookshelf.auth.service.AuthAdminService;
import com.lifelibrarians.lifebookshelf.exception.annotation.ApiErrorCodeExample;
import com.lifelibrarians.lifebookshelf.exception.status.AuthExceptionStatus;
import com.lifelibrarians.lifebookshelf.log.Logging;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/admin/auth")
@Tag(name = "관리자 인증 (admin-auth-api)", description = "관리자 전용 인증 관련 API")
@Logging
@FieldNameConstants
public class AuthAdminController {

    private final AuthAdminService authAdminService;

    @Operation(summary = "관리자 이메일 로그인", description = "관리자 권한을 가진 사용자만 로그인할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "accepted",
                    content = @Content(schema = @Schema(implementation = JwtLoginTokenDto.class))
            ),
    })
    @ApiErrorCodeExample(
            authExceptionStatuses = {
                    AuthExceptionStatus.INVALID_EMAIL_FORMAT,
                    AuthExceptionStatus.EMAIL_TOO_LONG,
                    AuthExceptionStatus.PASSWORD_FORMAT_ERROR,
                    AuthExceptionStatus.EMAIL_OR_PASSWORD_INCORRECT,
                    AuthExceptionStatus.EMAIL_NOT_VERIFIED,
                    AuthExceptionStatus.MEMBER_NOT_FOUND,
                    AuthExceptionStatus.MEMBER_ALREADY_WITHDRAWN,
                    AuthExceptionStatus.MEMBER_IS_NOT_ADMIN
            }
    )
    @PostMapping(value = "/email-login", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @OneWayEncryption({
            @TargetMapping(clazz = EmailLoginRequestDto.class, fields = {
                    EmailLoginRequestDto.Fields.password})
    })
    public JwtLoginTokenDto adminLogin(
            @Valid @ModelAttribute EmailLoginRequestDto requestDto
    ) {
        return authAdminService.adminEmailLogin(requestDto);
    }
}
