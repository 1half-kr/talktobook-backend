package com.lifelibrarians.lifebookshelf.app.controller;

import com.lifelibrarians.lifebookshelf.app.dto.request.CreateAppVersionRequestDto;
import com.lifelibrarians.lifebookshelf.app.dto.request.UpdateAppVersionRequestDto;
import com.lifelibrarians.lifebookshelf.app.dto.response.ReadAllAppVersionsResponseDto;
import com.lifelibrarians.lifebookshelf.app.dto.response.ReadAppVersionResponseDto;
import com.lifelibrarians.lifebookshelf.app.service.AppVersionFacadeService;
import com.lifelibrarians.lifebookshelf.exception.annotation.ApiErrorCodeExample;
import com.lifelibrarians.lifebookshelf.exception.status.AppVersionExceptionStatus;
import com.lifelibrarians.lifebookshelf.log.Logging;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v2/version")
@Tag(name = "앱 (App Version)", description = "앱 버전 관련 API")
@Logging
public class AppVersionController {

    private final AppVersionFacadeService appVersionFacadeService;

    @Operation(summary = "새로운 앱 버전 생성", description = "새로운 앱 버전을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "created"),
    })
    // TODO: 관리자 권한으로 변경 필요
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void createAppVersion(
            @RequestParam(value = "platform", defaultValue = "ANDROID") String platform,
            @Valid @ModelAttribute CreateAppVersionRequestDto requestDto
    ) {
        appVersionFacadeService.createAppVersion(platform, requestDto);
    }

    @Operation(summary = "특정 앱 버전 수정 요청", description = "특정 앱 버전의 메타 데이터를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ok"),
    })
    @ApiErrorCodeExample(
            appVersionExceptionStatuses = {
                    AppVersionExceptionStatus.APP_VERSION_NOT_FOUND
            }
    )
    @PatchMapping(value = "/{versionId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateAppVersion(
            @PathVariable("versionId") @Parameter(description = "앱 버전 ID") Long versionId,
            @Valid @ModelAttribute UpdateAppVersionRequestDto requestDto
            ) {
        appVersionFacadeService.updateAppVersion(versionId, requestDto);
    }

    @Operation(summary = "현재 앱 버전 조회", description = "현재 앱 버전을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ok"),
    })
    @ApiErrorCodeExample(
            appVersionExceptionStatuses = {
                    AppVersionExceptionStatus.APP_VERSION_NOT_FOUND
            }
    )
    @GetMapping("/current")
    @ResponseStatus(HttpStatus.OK)
    public ReadAppVersionResponseDto getCurrentAppVersion(
            @RequestParam(value = "platform", defaultValue = "ANDROID") String platform
    ) {
        return appVersionFacadeService.getCurrentAppVersion(platform);
    }

    @Operation(summary = "모든 앱 버전 조회", description = "존재하는 모든 앱 버전을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ok"),
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ReadAllAppVersionsResponseDto getAllAppVersions() {
        return appVersionFacadeService.getAllAppVersions();
    }
}
