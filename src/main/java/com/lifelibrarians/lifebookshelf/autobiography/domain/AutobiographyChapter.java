package com.lifelibrarians.lifebookshelf.autobiography.domain;

import com.lifelibrarians.lifebookshelf.chapter.domain.Chapter;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "autobiography_chapters")
@Getter
@ToString(callSuper = true, exclude = {"autobiography"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AutobiographyChapter {

    /* 고유 정보 { */
    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @JoinColumn(name = "title", nullable = true)
    private String title;

    @Lob
    @JoinColumn(name = "content", nullable = true)
    private String content;

    @Column
    @JoinColumn(name = "cover_image_url", nullable = true)
    private String coverImageUrl;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
    /* } 고유 정보 */

    /* 연관 정보 { */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autobiography_id", nullable = false)
    private Autobiography autobiography;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    /* } 연관 정보 */

    /* 생성자 { */
    protected AutobiographyChapter(String title, String content, String coverImageUrl,
                            LocalDateTime createdAt, LocalDateTime updatedAt, Member member, Autobiography autobiography) {
        this.title = title;
        this.content = content;
        this.coverImageUrl = coverImageUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.member = member;
        this.autobiography = autobiography;
    }
    public static AutobiographyChapter of(String title, String content, String coverImageUrl,
                                   LocalDateTime createdAt, LocalDateTime updatedAt, Member member, Autobiography autobiography) {
        return new AutobiographyChapter(title, content, coverImageUrl, createdAt, updatedAt, member, autobiography);
    }
    /* } 생성자 */

    /* 업데이트 메서드 { */
    public void updateAutoBiographyChapter(String title, String content, String preSignedImageUrl,
                                    LocalDateTime now) {
        if (title != null && !title.isEmpty()) {
            this.title = title;
        }
        if (content != null && !content.isEmpty()) {
            this.content = content;
        }
        if (preSignedImageUrl != null && !preSignedImageUrl.isEmpty()) {
            this.coverImageUrl = preSignedImageUrl;
        }
        this.updatedAt = now;
    }
    /* } 업데이트 메서드 */
}
