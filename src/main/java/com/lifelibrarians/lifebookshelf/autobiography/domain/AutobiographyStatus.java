package com.lifelibrarians.lifebookshelf.autobiography.domain;

import com.lifelibrarians.lifebookshelf.member.domain.Member;
import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "autobiography_statuses")
@Getter
@ToString(callSuper = true, exclude = {"member", "currentAutobiography"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AutobiographyStatus {

    /* 고유 정보 { */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AutobiographyStatusType status;
    /* } 고유 정보 */

    /* 연관 정보 { */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_autobiography_id", nullable = false)
    private Autobiography currentAutobiography;
    /* } 연관 정보 */

    /* 생성자 { */
    protected AutobiographyStatus(AutobiographyStatusType status,
                                  Member member,
                                  Autobiography currentAutobiography,
                                  LocalDateTime updatedAt) {
        this.status = status;
        this.member = member;
        this.currentAutobiography = currentAutobiography;
        this.updatedAt = updatedAt;
    }

    public static AutobiographyStatus of(AutobiographyStatusType status,
                                         Member member,
                                         Autobiography currentAutobiography,
                                         LocalDateTime updatedAt) {
        return new AutobiographyStatus(status, member, currentAutobiography, updatedAt);
    }
    /* } 생성자 */

    /* 비즈니스 메서드 { */
    public void updateStatusType(AutobiographyStatusType status,
                             LocalDateTime now) {
        this.status = status;
        this.updatedAt = now;
    }
    /* } 비즈니스 메서드 */
}
