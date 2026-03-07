package com.lifelibrarians.lifebookshelf.app.domain;

import com.lifelibrarians.lifebookshelf.autobiography.domain.Autobiography;
import com.lifelibrarians.lifebookshelf.chapter.domain.Chapter;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "app_versions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AppVersions {
    /* 고유 정보 { */
    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @JoinColumn(name = "platform", nullable = true)
    private PlatformType platform;

    @Column
    @JoinColumn(name = "version_code", nullable = false)
    private Integer versionCode;

    @Column
    @JoinColumn(name = "version_name", nullable = false)
    private String versionName;

    @Column
    @JoinColumn(name = "release_notes", nullable = true)
    private String releaseNotes;

    // 강제 업데이트 여부
    @Column
    @JoinColumn(name = "is_force_update", nullable = false)
    private Boolean isForceUpdate;

    @Column(nullable = false)
    private LocalDateTime releasedAt;
    /* } 고유 정보 */

    /* 생성자 { */
    protected AppVersions(PlatformType platform, Integer versionCode, String versionName, Boolean isForceUpdate, String releaseNotes, LocalDateTime releasedAt) {
        this.platform = platform;
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.isForceUpdate = isForceUpdate;
        this.releaseNotes = releaseNotes;
        this.releasedAt = releasedAt;
    }
    public static AppVersions of(PlatformType platform, Integer versionCode, String versionName, Boolean isForceUpdate, String releaseNotes, LocalDateTime releasedAt) {
        return new AppVersions(platform, versionCode, versionName, isForceUpdate, releaseNotes, releasedAt);
    }
    /* } 생성자 (V1) */

    /* 업데이트 메서드 { */
    public void updateVersionInfo(String versionName, Boolean isForceUpdate, String releaseNotes, LocalDateTime releasedAt) {
        this.versionName = versionName;
        this.isForceUpdate = isForceUpdate;
        this.releaseNotes = releaseNotes;
        this.releasedAt = releasedAt;
    }
    /* } 업데이트 메서드 */
}
