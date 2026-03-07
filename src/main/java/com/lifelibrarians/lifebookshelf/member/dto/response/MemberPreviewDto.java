package com.lifelibrarians.lifebookshelf.member.dto.response;

import com.lifelibrarians.lifebookshelf.member.domain.LoginType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "멤버 미리보기 정보")
@ToString
public class MemberPreviewDto {

    @Schema(description = "멤버 ID", example = "1")
    private final Long memberId;

    @Schema(description = "이메일", example = "user@example.com")
    private final String email;

    @Schema(description = "닉네임", example = "홍길동")
    private final String nickname;

    @Schema(description = "로그인 타입", example = "SOCIAL")
    private final LoginType loginType;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private final String profileImageUrl;

    @Schema(description = "가입일", example = "2023-01-01T00:00:00Z")
    private final LocalDateTime createdAt;

    @Schema(description = "멤버 메타데이터 정보")
    private final MemberMetadata metadata;

    @Schema(description = "멤버 활동 요약 정보")
    private final ActivitySummary activitySummary;

    @Builder
    @AllArgsConstructor
    @Getter
    @Schema(description = "멤버 메타데이터")
    @ToString
    public static class MemberMetadata {
        @Schema(description = "회원 실명", example = "홍길동")
        private final String name;

        @Schema(description = "성별", example = "MALE")
        private final String gender;

        @Schema(description = "학력", example = "Bachelor's Degree")
        private final String educationLevel;

        @Schema(description = "결혼 상태", example = "미혼")
        private final String maritalStatus;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    @Schema(description = "멤버 활동 요약")
    @ToString
    public static class ActivitySummary {
        @Schema(description = "수행한 인터뷰 횟수", example = "5")
        private final Integer interviewCount;

        @Schema(description = "생성된 자서전 개수", example = "3")
        private final Integer autobiographyCount;
    }
}
