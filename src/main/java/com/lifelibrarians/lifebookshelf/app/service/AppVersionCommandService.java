package com.lifelibrarians.lifebookshelf.app.service;

import com.lifelibrarians.lifebookshelf.app.domain.AppVersions;
import com.lifelibrarians.lifebookshelf.app.domain.MemberAppVersions;
import com.lifelibrarians.lifebookshelf.app.domain.PlatformType;
import com.lifelibrarians.lifebookshelf.app.dto.request.CreateAppVersionRequestDto;
import com.lifelibrarians.lifebookshelf.app.dto.request.PatchMemberAppVersionRequestDto;
import com.lifelibrarians.lifebookshelf.app.dto.request.UpdateAppVersionRequestDto;
import com.lifelibrarians.lifebookshelf.app.repository.AppVersionRepository;
import com.lifelibrarians.lifebookshelf.app.repository.MemberAppVersionRepository;
import com.lifelibrarians.lifebookshelf.exception.status.AppVersionExceptionStatus;
import com.lifelibrarians.lifebookshelf.exception.status.MemberExceptionStatus;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import com.lifelibrarians.lifebookshelf.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AppVersionCommandService {
    private final AppVersionRepository appVersionRepository;
    private final MemberAppVersionRepository memberAppVersionRepository;
    private final MemberRepository memberRepository;


    public void createAppVersion(String platform, CreateAppVersionRequestDto requestDto) {
        LocalDateTime now = LocalDateTime.now();

        PlatformType platformType = PlatformType.from(platform)
                .orElseThrow(AppVersionExceptionStatus.INVALID_PLATFORM_TYPE::toServiceException);


        // 가장 최신 버전 코드를 조회하여 +1 증가시킴
        int newVersionCode = appVersionRepository
                .findTopByPlatformOrderByVersionCodeDesc(platformType)
                .map(v -> v.getVersionCode() + 1)
                .orElse(1);

        AppVersions newAppVersion = AppVersions.of(
                platformType,
                newVersionCode,
                requestDto.getVersionName(),
                requestDto.getIsForceUpdate(),
                requestDto.getReleaseNotes(),
                now
        );

        appVersionRepository.save(newAppVersion);

        log.info("App version has been created: {}", newAppVersion);
    }

    public void updateAppVersion(Long versionId, UpdateAppVersionRequestDto requestDto) {
        LocalDateTime now = LocalDateTime.now();

        AppVersions appVersion = appVersionRepository.findById(versionId)
                .orElseThrow(AppVersionExceptionStatus.APP_VERSION_NOT_FOUND::toServiceException);

        appVersion.updateVersionInfo(
                requestDto.getVersionName(),
                requestDto.getIsForceUpdate(),
                requestDto.getReleaseNotes(),
                now
        );

        appVersionRepository.save(appVersion);

        log.info("App version has been updated: {}", appVersion);
    }
}
