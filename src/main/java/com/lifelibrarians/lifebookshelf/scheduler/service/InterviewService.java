package com.lifelibrarians.lifebookshelf.scheduler.service;

import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatus;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatusType;
import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyStatusRepository;
import com.lifelibrarians.lifebookshelf.interview.domain.Conversation;
import com.lifelibrarians.lifebookshelf.interview.domain.ConversationType;
import com.lifelibrarians.lifebookshelf.interview.domain.Interview;
import com.lifelibrarians.lifebookshelf.interview.repository.ConversationRepository;
import com.lifelibrarians.lifebookshelf.interview.repository.InterviewRepository;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import com.lifelibrarians.lifebookshelf.member.repository.MemberRepository;
import com.lifelibrarians.lifebookshelf.queue.publisher.InterviewSummaryPublisher;
import com.lifelibrarians.lifebookshelf.queue.dto.request.InterviewSummaryRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewService {

    private final AutobiographyStatusRepository autobiographyStatusRepository;
    private final InterviewRepository interviewRepository;
    private final ConversationRepository conversationRepository;
    private final MemberRepository memberRepository;
    private final InterviewSummaryPublisher interviewSummaryPublisher;

    @Transactional
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul") // 매일 자정
    public void createInterviewsForAllMembers() {
        log.info("[InterviewScheduler] Starting daily interview generation...");

        List<Member> allMembers = memberRepository.findAll();

        for (Member member : allMembers) {
            // 멤버의 자서전 상태 조회
            List<AutobiographyStatus> statusList =
                    autobiographyStatusRepository.findByMemberId(member.getId());

            if (statusList.isEmpty()) {
                log.warn("[InterviewScheduler] No AutobiographyStatus for member {}", member.getId());
                continue; // 다음 member로 넘어감
            }

            for (AutobiographyStatus status : statusList) {
                // creating / finish 상태 제외
                AutobiographyStatusType type = status.getStatus();
                LocalDateTime now = LocalDateTime.now();

                // creating, finish 제외
                if (type == AutobiographyStatusType.CREATING ||
                        type == AutobiographyStatusType.FINISH) {
                    continue;
                }

                if (status.getCurrentAutobiography() == null) {
                    log.error("[InterviewScheduler] No autobiography found for member {}", member.getId());
                    continue;
                }

                Optional<Interview> optInterview =
                        interviewRepository.findTopByAutobiographyIdOrderByCreatedAtDesc(
                                status.getCurrentAutobiography().getId()
                        );

                if (optInterview.isEmpty()) {
                    log.warn("[InterviewScheduler] No Interview found for member {}", member.getId());

                    // 이전 인터뷰가 비어있더라도 새로운 인터뷰 생성
                    Interview interview = Interview.ofV2(
                            now,
                            status.getCurrentAutobiography(),
                            member,
                            null         // summary
                    );

                    interviewRepository.save(interview);
                    log.info("[InterviewScheduler] Interview created for member {}", member.getId());

                    continue;
                }


                Interview lastInterview = optInterview.get();

                // last interview에서 가장 마지막 bot conversation 복사
                Conversation latestBotConversation = lastInterview.getInterviewConversations().stream()
                        .filter(c -> c.getConversationType() == ConversationType.BOT)
                        .max(Comparator.comparing(Conversation::getCreatedAt))
                        .orElseGet(() -> {
                            log.warn("No BOT conversation found for interviewId={}", lastInterview.getId());
                            return null;
                        });

                Interview interview = Interview.ofV2(
                        now,
                        status.getCurrentAutobiography(),
                        member,
                        null         // summary
                );

                interviewRepository.save(interview);
                log.info("[InterviewScheduler] Interview created for member {}", member.getId());

                // 마지막 대화 기록을 다음 생성한 interview에 추가하기
                if (latestBotConversation != null) {
                    // interview만 업데이트
                    latestBotConversation.updateInterview(interview);
                    conversationRepository.save(latestBotConversation);

                    log.info("[InterviewScheduler] Last Conversation created for interview {}", interview.getId());

                // interview summary 큐에발행
                List<InterviewSummaryRequestDto.Conversations> conversationPairs =
                        lastInterview.getInterviewConversations().stream()
                                .sorted(Comparator.comparing(Conversation::getCreatedAt)) // 시간순 정렬
                                .collect(Collectors.groupingBy(Conversation::getConversationType))
                                .entrySet().stream()
                                .flatMap(entry -> entry.getValue().stream())
                                .sorted(Comparator.comparing(Conversation::getCreatedAt)) // grouping으로 흐트러질 수 있어 재정렬
                                .collect(ArrayList::new,
                                        (list, conv) -> {
                                            if (conv.getConversationType() == ConversationType.BOT) {
                                                list.add(InterviewSummaryRequestDto.Conversations.builder()
                                                        .question(conv.getContent())
                                                        .conversation(null) // 아직 HUMAN 안 나왔음
                                                        .build());
                                            } else {
                                                // 마지막에 들어간 BOT의 conversation 채우기
                                                InterviewSummaryRequestDto.Conversations last =
                                                        list.get(list.size() - 1);

                                                list.set(list.size() - 1,
                                                        InterviewSummaryRequestDto.Conversations.builder()
                                                                .question(last.getQuestion())
                                                                .conversation(conv.getContent())
                                                                .build()
                                                );
                                            }
                                        },
                                        ArrayList::addAll
                                );

                // null conversation 제거
                List<InterviewSummaryRequestDto.Conversations> validConversations = conversationPairs.stream()
                        .filter(conv -> conv.getConversation() != null && !conv.getConversation().trim().isEmpty())
                        .collect(Collectors.toList());

                InterviewSummaryRequestDto dto = InterviewSummaryRequestDto.builder()
                        .interviewId(lastInterview.getId())
                        .userId(member.getId())
                        .conversations(validConversations)
                        .build();
                
                // 큐에 인터뷰 summary를 방행합니다.
                interviewSummaryPublisher.publishInterviewSummaryRequest(dto);

                } else {
                    log.warn("[InterviewScheduler] No BOT conversation to copy for interview {}", interview.getId());
                }
            }
        }
    }
}