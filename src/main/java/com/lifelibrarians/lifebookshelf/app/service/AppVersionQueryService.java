package com.lifelibrarians.lifebookshelf.app.service;

import com.lifelibrarians.lifebookshelf.app.domain.AppVersions;
import com.lifelibrarians.lifebookshelf.app.domain.PlatformType;
import com.lifelibrarians.lifebookshelf.app.dto.response.ReadAllAppVersionsResponseDto;
import com.lifelibrarians.lifebookshelf.app.dto.response.ReadAppVersionResponseDto;
import com.lifelibrarians.lifebookshelf.app.repository.AppVersionRepository;
import com.lifelibrarians.lifebookshelf.exception.status.AppVersionExceptionStatus;
import com.lifelibrarians.lifebookshelf.log.Logging;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Logging
public class AppVersionQueryService {
    private final AppVersionRepository appVersionRepository;

    // 모든 app versions를 조회
    public ReadAllAppVersionsResponseDto getAllAppVersions() {
        List<AppVersions> appVersions = appVersionRepository.findAll();

        List<ReadAppVersionResponseDto> responseDtos = appVersions.stream()
                .map(appVersion -> ReadAppVersionResponseDto.builder()
                        .id(appVersion.getId())
                        .versionName(appVersion.getVersionName())
                        .versionCode(appVersion.getVersionCode())
                        .platform(appVersion.getPlatform().name())
                        .isForceUpdate(appVersion.getIsForceUpdate())
                        .releaseNotes(appVersion.getReleaseNotes())
                        .releasedAt(appVersion.getReleasedAt().toString())
                        .build())
                .collect(Collectors.toList());

        return ReadAllAppVersionsResponseDto.builder()
                .appVersions(responseDtos)
                .build();
    }

    public ReadAppVersionResponseDto getCurrentAppVersion(String platform) {
        PlatformType platformType = PlatformType.from(platform)
                .orElseThrow(AppVersionExceptionStatus.INVALID_PLATFORM_TYPE::toServiceException);

        AppVersions appVersion = appVersionRepository.findTopByPlatformOrderByVersionCodeDesc(platformType)
                .orElseThrow(AppVersionExceptionStatus.APP_VERSION_NOT_FOUND::toServiceException);

        // 현재 앱 버전 정보 반환 (예시로 하드코딩된 값 사용)
        return ReadAppVersionResponseDto.builder()
                .id(appVersion.getId())
                .versionName(appVersion.getVersionName())
                .versionCode(appVersion.getVersionCode())
                .platform(appVersion.getPlatform().name())
                .isForceUpdate(appVersion.getIsForceUpdate())
                .releaseNotes(appVersion.getReleaseNotes())
                .releasedAt(appVersion.getReleasedAt().toString())
                .build();
    }
}
