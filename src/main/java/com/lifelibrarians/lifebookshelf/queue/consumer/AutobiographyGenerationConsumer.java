package com.lifelibrarians.lifebookshelf.queue.consumer;

import com.lifelibrarians.lifebookshelf.autobiography.domain.Autobiography;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyChapter;
import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyChapterRepository;
import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyRepository;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import com.lifelibrarians.lifebookshelf.member.repository.MemberRepository;
import com.lifelibrarians.lifebookshelf.queue.dto.response.AutobiographyGenerateResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class AutobiographyGenerationConsumer {

    private final AutobiographyRepository autobiographyRepository;
    private final MemberRepository memberRepository;
    private final AutobiographyChapterRepository autobiographyChapterRepository;

    @SqsListener("${sqs.queue.url.autobiography-result}")
    public void receive(@Payload AutobiographyGenerateResponseDto dto) {
        log.info("[RECEIVE_AUTOBIOGRAPHY] 자서전 챕터 수신 - autobiographyId: {}, userId: {}, cycleId: {}, step: {}",
                dto.getAutobiographyId(), dto.getUserId(), dto.getCycleId(), dto.getStep());

        LocalDateTime now = LocalDateTime.now();

        if (dto.getCycleId() == null || dto.getCycleId().isEmpty()) {
            log.warn("[RECEIVE_AUTOBIOGRAPHY] cycleId 없음 - autobiographyId: {}", dto.getAutobiographyId());
            return;
        }

        Autobiography autobiography = autobiographyRepository.findById(dto.getAutobiographyId())
                .orElseThrow(() -> new RuntimeException("Autobiography not found: " + dto.getAutobiographyId()));

        Member member = memberRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Member not found: " + dto.getUserId()));

        AutobiographyChapter chapter = AutobiographyChapter.of(
                dto.getTitle(),
                dto.getContent(),
                null,
                now,
                now,
                member,
                autobiography
        );

        autobiographyChapterRepository.save(chapter);
        log.info("[RECEIVE_AUTOBIOGRAPHY] 챕터 저장 완료 - chapterId: {}, step: {}", chapter.getId(), dto.getStep());
    }
}
