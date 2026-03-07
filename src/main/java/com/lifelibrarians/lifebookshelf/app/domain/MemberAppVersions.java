package com.lifelibrarians.lifebookshelf.app.domain;

import com.lifelibrarians.lifebookshelf.member.domain.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_app_versions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberAppVersions {
    /* 고유 정보 { */
    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
    /* } 고유 정보 */

    /* 연관 관계 { */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToOne
    @JoinColumn(name = "app_version_id", nullable = false)
    private AppVersions appVersions;
    /* } 연관 관계 */

    /* 생성자 { */
    protected MemberAppVersions(
            Member member, AppVersions appVersions, LocalDateTime updatedAt
    ) {
        this.member = member;
        this.appVersions = appVersions;
        this.updatedAt = updatedAt;
    }
    public static MemberAppVersions of(
            Member member, AppVersions appVersions, LocalDateTime updatedAt) {
        return new MemberAppVersions(member, appVersions, updatedAt);
    }
    /* } 생성자 (V1) */

    /* 업데이트 메서드 { */
    public void updateMemberAppVersion(
            AppVersions appVersions, LocalDateTime updatedAt
    ) {
        this.appVersions = appVersions;
        this.updatedAt = updatedAt;
    }
    /* } 업데이트 메서드 */
}
