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
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class AutobiographyGenerationConsumer {

    private final AutobiographyRepository autobiographyRepository;
    private final MemberRepository memberRepository;
    private final AutobiographyChapterRepository autobiographyChapterRepository;

    @RabbitListener(queues = "autobiography.trigger.result.queue")
    public void receive(AutobiographyGenerateResponseDto dto) {
        log.info("[RECEIVE_AUTOBIOGRAPHY] 자서전 챕터 수신 - autobiographyId: {}, userId: {}, cycleId: {}, step: {}", 
                dto.getAutobiographyId(), dto.getUserId(), dto.getCycleId(), dto.getStep());
        
        LocalDateTime now = LocalDateTime.now();

        // Aggregator에서 온 메시지인지 확인 (title, content가 없으면 Aggregator 메시지)
        if (dto.getTitle() == null && dto.getContent() == null) {
            log.warn("[RECEIVE_AUTOBIOGRAPHY] Aggregator 메시지 거부 - autobiographyId: {}", dto.getAutobiographyId());
            return;
        }

        // cycleId가 없으면 메시지 거부 (새로운 사이클 관리 시스템 필수)
        if (dto.getCycleId() == null || dto.getCycleId().isEmpty()) {
            log.warn("[RECEIVE_AUTOBIOGRAPHY] cycleId 없음 - autobiographyId: {}", dto.getAutobiographyId());
            return;
        }

        Autobiography autobiography = autobiographyRepository.findById(dto.getAutobiographyId())
                .orElseThrow(() -> new RuntimeException("Autobiography not found: " + dto.getAutobiographyId()));

        Member member = memberRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Member not found: " + dto.getUserId()));

        // autobiography에 대한 chapter 생성
        AutobiographyChapter chapter = AutobiographyChapter.of(
                dto.getTitle(),
                dto.getContent(),
                null, // step을 chapterOrder로 사용
                now,
                now,
                member,
                autobiography
        );

        autobiographyChapterRepository.save(chapter);
        log.info("[RECEIVE_AUTOBIOGRAPHY] 챕터 저장 완료 - chapterId: {}, step: {}", chapter.getId(), dto.getStep());
    }
}
