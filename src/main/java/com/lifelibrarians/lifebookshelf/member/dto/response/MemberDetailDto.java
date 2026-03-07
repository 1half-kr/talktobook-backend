package com.lifelibrarians.lifebookshelf.member.dto.response;

import com.lifelibrarians.lifebookshelf.member.domain.LoginType;
import com.lifelibrarians.lifebookshelf.member.domain.MemberRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "멤버 상세 정보")
@ToString
public class MemberDetailDto {

    @Schema(description = "멤버 ID", example = "1")
    private final Long memberId;

    @Schema(description = "이메일", example = "user@example.com")
    private final String email;

    @Schema(description = "닉네임", example = "홍길동")
    private final String nickname;

    @Schema(description = "로그인 타입", example = "SOCIAL")
    private final LoginType loginType;

    @Schema(description = "멤버 역할", example = "MEMBER")
    private final MemberRole role;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private final String profileImageUrl;

    @Schema(description = "가입일", example = "2023-01-01T00:00:00Z")
    private final LocalDateTime createdAt;

    @Schema(description = "닉네임 수정일", example = "2023-01-02T00:00:00Z")
    private final LocalDateTime nicknameUpdatedAt;

    @Schema(description = "삭제일 (null이면 활성 상태)", example = "2023-12-31T23:59:59Z")
    private final LocalDateTime deletedAt;

    @Schema(description = "활성 상태 여부", example = "true")
    public boolean isActive() {
        return deletedAt == null;
    }
}
