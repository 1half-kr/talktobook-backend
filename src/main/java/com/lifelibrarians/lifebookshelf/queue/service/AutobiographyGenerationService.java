package com.lifelibrarians.lifebookshelf.queue.service;

import com.lifelibrarians.lifebookshelf.autobiography.domain.Autobiography;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatus;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatusType;
import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyRepository;
import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyStatusRepository;
import com.lifelibrarians.lifebookshelf.interview.domain.Conversation;
import com.lifelibrarians.lifebookshelf.interview.domain.ConversationType;
import com.lifelibrarians.lifebookshelf.interview.repository.ConversationRepository;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import com.lifelibrarians.lifebookshelf.member.repository.MemberRepository;
import com.lifelibrarians.lifebookshelf.queue.dto.request.AutobiographyGenerateRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutobiographyGenerationService {
    
    private final AutobiographyStatusRepository autobiographyStatusRepository;
    private final AutobiographyRepository autobiographyRepository;
    private final ConversationRepository conversationRepository;
    private final MemberRepository memberRepository;
    private final RabbitTemplate rabbitTemplate;
    
    private static final int BATCH_SIZE = 10; // 한 번에 처리할 conversation 수
    private static final String AUTOBIOGRAPHY_GENERATION_QUEUE = "autobiography.generation.queue";
    
    @Transactional(readOnly = true)
    public void processCreatingStatus(Long memberId) {
        log.info("[PROCESS_CREATING_STATUS] CREATING 상태 처리 시작 - memberId: {}", memberId);
        
        // CREATING 상태인 자서전 상태 조회
        AutobiographyStatus status = autobiographyStatusRepository
                .findTopByMemberIdAndStatusInOrderByUpdatedAtDesc(memberId, List.of(AutobiographyStatusType.CREATING))
            .orElseThrow(() -> new RuntimeException("CREATING 상태의 자서전을 찾을 수 없습니다."));
        
        Long autobiographyId = status.getCurrentAutobiography().getId();
        log.info("[PROCESS_CREATING_STATUS] 자서전 조회 완료 - autobiographyId: {}", autobiographyId);
        
        // 해당 member의 모든 HUMAN 타입 conversation 조회
        List<Conversation> humanConversations = getAllHumanConversations(autobiographyId);
        log.info("[PROCESS_CREATING_STATUS] HUMAN 대화 조회 완료 - conversationsCount: {}", humanConversations.size());
        
        // materials 기준으로 계산 및 그룹화
        List<List<Conversation>> conversationBatches = groupConversationsByMaterials(humanConversations);
        log.info("[PROCESS_CREATING_STATUS] 대화 그룹화 완료 - batchesCount: {}", conversationBatches.size());
        
        // 각 그룹별로 큐에 발행
        conversationBatches.forEach(batch -> publishBatch(memberId, autobiographyId, batch));
        log.info("[PROCESS_CREATING_STATUS] CREATING 상태 처리 완료 - memberId: {}, autobiographyId: {}", memberId, autobiographyId);
    }
    
    private List<Conversation> getAllHumanConversations(Long autobiographyId) {
        log.info("[GET_ALL_HUMAN_CONVERSATIONS] HUMAN 대화 조회 시작 - autobiographyId: {}", autobiographyId);
        
        // 페이징으로 모든 HUMAN 타입 conversation 조회
        List<Conversation> conversations = conversationRepository.findAll()
            .stream()
            .filter(c -> c.getInterview().getAutobiography().getId().equals(autobiographyId))
//            .filter(c -> c.getConversationType() == ConversationType.HUMAN)
            .collect(Collectors.toList());
        
        log.info("[GET_ALL_HUMAN_CONVERSATIONS] HUMAN 대화 조회 완료 - autobiographyId: {}, count: {}", 
                autobiographyId, conversations.size());
        
        return conversations;
    }
    
    private List<List<Conversation>> groupConversationsByMaterials(List<Conversation> conversations) {
        // materials 기준으로 계산 (예: materials 길이, 특정 키워드 등)
        return new ArrayList<>(conversations.stream()
                .collect(Collectors.groupingBy(this::calculateMaterialsGroup))
                .values());
    }
    
    private String calculateMaterialsGroup(Conversation conversation) {
        // materials 기준 계산 로직 (예시)
        String materials = conversation.getMaterials();
        if (materials == null || materials.isEmpty()) {
            return "empty";
        }
        
        // materials 길이나 특정 패턴으로 그룹 결정
        int length = materials.length();
        if (length < 100) return "short";
        else if (length < 500) return "medium";
        else return "long";
    }
    
    private void publishBatch(Long memberId, Long autobiographyId, List<Conversation> conversations) {
        log.info("[PUBLISH_BATCH] 배치 발행 시작 - memberId: {}, autobiographyId: {}, conversationsCount: {}", 
                memberId, autobiographyId, conversations.size());
        
        // Conversation을 DTO로 변환
        List<AutobiographyGenerateRequestDto.InterviewAnswer> answers = conversations.stream()
            .map(c -> AutobiographyGenerateRequestDto.InterviewAnswer.builder()
                .content(c.getContent())
                .conversationType(c.getConversationType().name())
                .build())
            .collect(Collectors.toList());

        // TODO:: 추후 category를 정상적으로 계산하는 함수 추가
        String category = calculateMaterialsGroup(conversations.get(0));
        log.info("[PUBLISH_BATCH] 카테고리 계산 완료 - category: {}", category);

        // 사용자 정보와 자서전 정보는 별도로 조회하여 설정
        AutobiographyGenerateRequestDto request = AutobiographyGenerateRequestDto.builder()
            .autobiographyId(autobiographyId)
            .userId(memberId)
            .userInfo(getUserInfo(memberId))
            .autobiographyInfo(getAutobiographyInfo(autobiographyId, category))
            .answers(answers)
            .build();
        
        // 큐에 발행
        rabbitTemplate.convertAndSend(AUTOBIOGRAPHY_GENERATION_QUEUE, request);
        log.info("[PUBLISH_BATCH] 배치 발행 완료 - autobiographyId: {}, answersCount: {}", autobiographyId, answers.size());
    }
    
    private AutobiographyGenerateRequestDto.UserInfo getUserInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new RuntimeException("Member not found"));

        // Member 정보 조회하여 UserInfo 생성
        return AutobiographyGenerateRequestDto.UserInfo.builder()
            .gender(member.getMemberMemberMetadata().getGender().name())
            .occupation(member.getMemberMemberMetadata().getOccupation())
            .ageGroup(member.getMemberMemberMetadata().getAgeGroup())
            .build();
    }
    
    private AutobiographyGenerateRequestDto.AutobiographyInfo getAutobiographyInfo(Long autobiographyId, String category) {
        Autobiography autobiography = autobiographyRepository.findById(autobiographyId)
            .orElseThrow(() -> new RuntimeException("Autobiography not found"));

        // Autobiography 정보 조회하여 AutobiographyInfo 생성
        return AutobiographyGenerateRequestDto.AutobiographyInfo.builder()
            .theme(autobiography.getTheme())
            .reason(autobiography.getReason())
            .category(category)
            .build();
    }
}
