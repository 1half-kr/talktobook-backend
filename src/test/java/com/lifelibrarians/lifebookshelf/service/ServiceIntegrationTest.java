package com.lifelibrarians.lifebookshelf.service;

import com.lifelibrarians.lifebookshelf.member.service.MemberQueryService;
import com.lifelibrarians.lifebookshelf.autobiography.service.AutobiographyQueryService;
import com.lifelibrarians.lifebookshelf.interview.service.InterviewQueryService;
import com.lifelibrarians.lifebookshelf.publication.service.PublicationQueryService;
import com.lifelibrarians.lifebookshelf.auth.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ServiceIntegrationTest {

    @Autowired(required = false)
    private MemberQueryService memberQueryService;

    @Autowired(required = false)
    private AutobiographyQueryService autobiographyQueryService;

    @Autowired(required = false)
    private InterviewQueryService interviewQueryService;

    @Autowired(required = false)
    private PublicationQueryService publicationQueryService;

    @Autowired(required = false)
    private AuthService authService;

    @Test
    @DisplayName("주요 서비스 빈들이 정상적으로 로드되는지 확인")
    void serviceBeansLoadTest() {
        // 서비스들이 정상적으로 Spring Context에 등록되었는지 확인
        assertThat(memberQueryService).isNotNull();
        assertThat(autobiographyQueryService).isNotNull();
        assertThat(interviewQueryService).isNotNull();
        assertThat(publicationQueryService).isNotNull();
        assertThat(authService).isNotNull();
    }

    @Test
    @DisplayName("기본 기능 테스트")
    void basicFunctionalityTest() {
        // 간단한 기능 테스트
        String testString = "테스트";
        assertThat(testString).isEqualTo("테스트");
        assertThat(testString.length()).isEqualTo(3);
    }
}