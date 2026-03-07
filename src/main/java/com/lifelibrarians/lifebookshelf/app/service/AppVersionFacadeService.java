package com.lifelibrarians.lifebookshelf.app.service;

import com.lifelibrarians.lifebookshelf.app.dto.request.CreateAppVersionRequestDto;
import com.lifelibrarians.lifebookshelf.app.dto.request.PatchMemberAppVersionRequestDto;
import com.lifelibrarians.lifebookshelf.app.dto.request.UpdateAppVersionRequestDto;
import com.lifelibrarians.lifebookshelf.app.dto.response.ReadAllAppVersionsResponseDto;
import com.lifelibrarians.lifebookshelf.app.dto.response.ReadAppVersionResponseDto;
import com.lifelibrarians.lifebookshelf.log.Logging;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Logging
public class AppVersionFacadeService {
    private final AppVersionQueryService appVersionQueryService;
    private final AppVersionCommandService appVersionCommandService;

    public void createAppVersion(String platform, CreateAppVersionRequestDto requestDto) {
        appVersionCommandService.createAppVersion(platform, requestDto);
    }

    public void updateAppVersion(Long versionId, UpdateAppVersionRequestDto requestDto) {
        appVersionCommandService.updateAppVersion(versionId, requestDto);
    }

    public ReadAppVersionResponseDto getCurrentAppVersion(String platform) {
        return appVersionQueryService.getCurrentAppVersion(platform);
    }

    public ReadAllAppVersionsResponseDto getAllAppVersions() {
        return appVersionQueryService.getAllAppVersions();
    }
}
