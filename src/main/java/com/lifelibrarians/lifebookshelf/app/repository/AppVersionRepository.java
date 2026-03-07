package com.lifelibrarians.lifebookshelf.app.repository;

import com.lifelibrarians.lifebookshelf.app.domain.AppVersions;
import com.lifelibrarians.lifebookshelf.app.domain.PlatformType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppVersionRepository extends JpaRepository<AppVersions, Long> {

    // 선택한 플랫폼에서 가장 최신 버전 코드를 가지고 있는 AppVersions 엔티티를 찾는 메서드
    Optional<AppVersions> findTopByPlatformOrderByVersionCodeDesc(PlatformType platform);

    // 특정 플랫폼과 버전 코드로 AppVersions 엔티티를 찾는 메서드
    Optional<AppVersions> findByPlatformAndVersionCode(PlatformType platform, Integer versionCode);
}
