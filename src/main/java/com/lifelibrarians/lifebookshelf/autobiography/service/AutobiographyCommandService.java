package com.lifelibrarians.lifebookshelf.autobiography.service;

import com.lifelibrarians.lifebookshelf.autobiography.domain.Autobiography;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyChapter;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatus;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatusType;
import com.lifelibrarians.lifebookshelf.autobiography.dto.request.AutobiographyInitRequestDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.request.AutobiographyUpdateRequestDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.request.CoShowAutobiographyGenerateRequestDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.response.AutobiographyInitResponseDto;
import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyChapterRepository;
import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyRepository;
import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyStatusRepository;
import com.lifelibrarians.lifebookshelf.classification.service.ClassificationInitService;
import com.lifelibrarians.lifebookshelf.exception.status.AuthExceptionStatus;
import com.lifelibrarians.lifebookshelf.exception.status.AutobiographyExceptionStatus;
import com.lifelibrarians.lifebookshelf.exception.status.InterviewExceptionStatus;
import com.lifelibrarians.lifebookshelf.image.service.ImageService;
import com.lifelibrarians.lifebookshelf.interview.domain.Conversation;
import com.lifelibrarians.lifebookshelf.interview.domain.ConversationType;
import com.lifelibrarians.lifebookshelf.interview.domain.Interview;
import com.lifelibrarians.lifebookshelf.interview.domain.InterviewQuestion;
import com.lifelibrarians.lifebookshelf.interview.repository.ConversationRepository;
import com.lifelibrarians.lifebookshelf.interview.repository.InterviewQuestionRepository;
import com.lifelibrarians.lifebookshelf.interview.repository.InterviewRepository;
import com.lifelibrarians.lifebookshelf.log.Logging;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.lifelibrarians.lifebookshelf.member.domain.*;
import com.lifelibrarians.lifebookshelf.member.repository.MemberMetadataRepository;
import com.lifelibrarians.lifebookshelf.member.repository.MemberRepository;
import com.lifelibrarians.lifebookshelf.member.repository.PasswordMemberRepository;
import com.lifelibrarians.lifebookshelf.queue.service.AutobiographyCompletionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
@Logging
public class AutobiographyCommandService {

	private final AutobiographyRepository autobiographyRepository;
    private final AutobiographyStatusRepository autobiographyStatusRepository;
    private final AutobiographyChapterRepository autobiographyChapterRepository;
    private final InterviewRepository interviewRepository;
    private final MemberRepository memberRepository;
	private final ImageService imageService;

    // TODO: CoShow용 (나중에 제거하세요.)
    private final MemberMetadataRepository memberMetadataRepository;
    private final ConversationRepository conversationRepository;
    private final PasswordMemberRepository passwordMemberRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;

    private final ClassificationInitService classificationInitService;
    private final AutobiographyCompletionService autobiographyCompletionService;

	@Value("${images.path.bio-cover}")
	public String BIO_COVER_IMAGE_DIR;

    public AutobiographyInitResponseDto initAutobiography(Long memberId, AutobiographyInitRequestDto requestDto) {
        log.info("[INIT_AUTOBIOGRAPHY] 자서전 초기화 시작 - memberId: {}, theme: {}", memberId, requestDto.getTheme());
        
        Member member = memberRepository.findById(memberId)
                .orElseThrow(AuthExceptionStatus.MEMBER_NOT_FOUND::toServiceException);

        LocalDateTime now = LocalDateTime.now();

        if (requestDto.getReason() != null && requestDto.getReason().length() > 500) {
            log.warn("[INIT_AUTOBIOGRAPHY] 자서전 이유 길이 초과 - memberId: {}, length: {}", memberId, requestDto.getReason().length());
            throw AutobiographyExceptionStatus.AUTOBIOGRAPHY_REASON_LENGTH_EXCEEDED.toServiceException();
        }

        Autobiography autobiography = Autobiography.ofV2(
                null,
                null,
                null,

                requestDto.getTheme(),
                requestDto.getReason(),
                now,
                now,
                member
        );
        // save init autobiography
        Autobiography savedAutobiography = autobiographyRepository.save(autobiography);
        log.info("[INIT_AUTOBIOGRAPHY] 자서전 저장 완료 - autobiographyId: {}", savedAutobiography.getId());

        AutobiographyStatus autobiographyStatus = AutobiographyStatus.of(
                AutobiographyStatusType.EMPTY,
                member,
                autobiography,
                now
        );

        autobiographyStatusRepository.save(autobiographyStatus);
        log.info("[INIT_AUTOBIOGRAPHY] 자서전 상태 저장 완료 - status: {}", AutobiographyStatusType.EMPTY);

        // AI 데이터 기반으로 분류 체계 초기화
        classificationInitService.initializeFromAiData(autobiography);
        log.info("[INIT_AUTOBIOGRAPHY] 분류 체계 초기화 완료 - autobiographyId: {}", savedAutobiography.getId());

        // save init interview
        Interview interview = Interview.ofV2(
                now,
                autobiography,
                member,
                null
        );

        Interview savedInterview = interviewRepository.save(interview);
        log.info("[INIT_AUTOBIOGRAPHY] 인터뷰 저장 완료 - interviewId: {}", savedInterview.getId());
        log.info("[INIT_AUTOBIOGRAPHY] 자서전 초기화 완료 - autobiographyId: {}, interviewId: {}", savedAutobiography.getId(), savedInterview.getId());

        return AutobiographyInitResponseDto.builder()
                .autobiographyId(savedAutobiography.getId())
                .interviewId(savedInterview.getId())
                .build();
    }

	public void patchAutobiography(Long memberId, Long autobiographyId, AutobiographyUpdateRequestDto requestDto) {
        log.info("[PATCH_AUTOBIOGRAPHY] 자서전 챕터 수정 시작 - memberId: {}, chapterId: {}", memberId, autobiographyId);
        
        AutobiographyChapter autobiographyChapter = autobiographyChapterRepository.findById(autobiographyId)
                .orElseThrow(AutobiographyExceptionStatus.AUTOBIOGRAPHY_CHAPTER_NOT_FOUND::toServiceException);
        
        if (!autobiographyChapter.getAutobiography().getMember().getId().equals(memberId)) {
            log.warn("[PATCH_AUTOBIOGRAPHY] 자서전 소유자 불일치 - memberId: {}, ownerId: {}", memberId, autobiographyChapter.getAutobiography().getMember().getId());
            throw AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_OWNER.toServiceException();
        }

        LocalDateTime now = LocalDateTime.now();

		String preSignedImageUrl = null;
		if (!Objects.isNull(requestDto.getPreSignedCoverImageUrl()) && !requestDto.getPreSignedCoverImageUrl().isBlank()) {
			preSignedImageUrl = imageService.parseImageUrl(requestDto.getPreSignedCoverImageUrl(), BIO_COVER_IMAGE_DIR);
			log.info("[PATCH_AUTOBIOGRAPHY] 커버 이미지 URL 파싱 완료 - chapterId: {}", autobiographyId);
		}
		autobiographyChapter.updateAutoBiographyChapter(requestDto.getTitle(), requestDto.getContent(), preSignedImageUrl, now);
		log.info("[PATCH_AUTOBIOGRAPHY] 자서전 챕터 수정 완료 - chapterId: {}", autobiographyId);
	}

    public void patchReasonAutobiography(Long memberId, Long autobiographyId, AutobiographyInitRequestDto requestDto) {
        log.info("[PATCH_REASON_AUTOBIOGRAPHY] 자서전 이유 수정 시작 - memberId: {}, autobiographyId: {}", memberId, autobiographyId);
        
        Autobiography autobiography = autobiographyRepository.findById(autobiographyId)
                .orElseThrow(AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND::toServiceException);
        
        if (!autobiography.getMember().getId().equals(memberId)) {
            log.warn("[PATCH_REASON_AUTOBIOGRAPHY] 자서전 소유자 불일치 - memberId: {}, ownerId: {}", memberId, autobiography.getMember().getId());
            throw AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_OWNER.toServiceException();
        }

        if (requestDto.getReason() != null && requestDto.getReason().length() > 500) {
            log.warn("[PATCH_REASON_AUTOBIOGRAPHY] 자서전 이유 길이 초과 - memberId: {}, length: {}", memberId, requestDto.getReason().length());
            throw AutobiographyExceptionStatus.AUTOBIOGRAPHY_REASON_LENGTH_EXCEEDED.toServiceException();
        }

        LocalDateTime now = LocalDateTime.now();

        autobiography.updateAutoBiographyV2(autobiography.getTitle(), autobiography.getContent(), autobiography.getCoverImageUrl(), autobiography.getTheme(), requestDto.getReason(), now);
        log.info("[PATCH_REASON_AUTOBIOGRAPHY] 자서전 이유 수정 완료 - autobiographyId: {}", autobiographyId);
    }

    @Async
    public void requestAutobiographyGenerate(Long memberId, Long autobiographyId, CoShowAutobiographyGenerateRequestDto requestDto) {
        log.info("[REQUEST_AUTOBIOGRAPHY_GENERATE] 자서전 생성 요청 시작 (비동기) - memberId: {}, autobiographyId: {}, name: {}", memberId, autobiographyId, requestDto.getName());
        
        Autobiography autobiography = autobiographyRepository.findById(autobiographyId)
                .orElseThrow(AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND::toServiceException);

        /*
        AutobiographyStatus autobiographyStatus = autobiographyStatusRepository
                .findTopByMemberIdAndStatusInOrderByUpdatedAtDesc(
                        memberId,
                        List.of(AutobiographyStatusType.EMPTY, AutobiographyStatusType.PROGRESSING, AutobiographyStatusType.ENOUGH)
                )
                .orElseThrow(AutobiographyExceptionStatus.AUTOBIOGRAPHY_STATUS_NOT_FOUND::toServiceException);
*/

        if (!autobiography.getMember().getId().equals(memberId)) {
            log.warn("[REQUEST_AUTOBIOGRAPHY_GENERATE] 자서전 소유자 불일치 - memberId: {}, ownerId: {}", memberId, autobiography.getMember().getId());
            throw AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_OWNER.toServiceException();
        }

        if (autobiography.getAutobiographyStatus().getStatus() != AutobiographyStatusType.ENOUGH) {
            log.warn("[REQUEST_AUTOBIOGRAPHY_GENERATE] 자서전 상태 불충분 - autobiographyId: {}, status: {}", autobiographyId, autobiography.getAutobiographyStatus().getStatus());
            throw AutobiographyExceptionStatus.AUTOBIOGRAPHY_ENOUTH_STATUS_NOT_FOUND.toServiceException();
        }

        // 자서전 생성 요청
        log.info("[REQUEST_AUTOBIOGRAPHY_GENERATE] 자서전 생성 큐 전송 시작 - autobiographyId: {}", autobiographyId);
        autobiographyCompletionService.triggerPublicationRequest(autobiography, requestDto.getName());
        log.info("[REQUEST_AUTOBIOGRAPHY_GENERATE] 자서전 생성 요청 완료 - autobiographyId: {}", autobiographyId);
    }

    public void patchAutobiographyStatus(Long memberId, Long autobiographyId, String status) {
        log.info("[PATCH_AUTOBIOGRAPHY_STATUS] 자서전 상태 변경 시작 - memberId: {}, autobiographyId: {}, requestedStatus: {}", memberId, autobiographyId, status);
        
        Autobiography autobiography = autobiographyRepository.findById(autobiographyId)
                .orElseThrow(AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND::toServiceException);

        /*
        AutobiographyStatus autobiographyStatus = autobiographyStatusRepository
                .findTopByMemberIdAndStatusInOrderByUpdatedAtDesc(
                        memberId,
                        List.of(AutobiographyStatusType.EMPTY, AutobiographyStatusType.PROGRESSING)
                )
                .orElseThrow(AutobiographyExceptionStatus.AUTOBIOGRAPHY_STATUS_NOT_FOUND::toServiceException);
*/

        log.info("[PATCH_AUTOBIOGRAPHY_STATUS] 현재 상태 확인 - autobiographyId: {}, currentStatus: {}", autobiographyId, autobiography.getAutobiographyStatus().getStatus());
        
        if (!autobiography.getMember().getId().equals(memberId)) {
            log.warn("[PATCH_AUTOBIOGRAPHY_STATUS] 자서전 소유자 불일치 - memberId: {}, ownerId: {}", memberId, autobiography.getMember().getId());
            throw AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_OWNER.toServiceException();
        }
        // TODO: FINISH, CREATING인 경우, 상태 변경이 불가능합니다.

        AutobiographyStatusType newStatus = AutobiographyStatusType.valueOf(status);

        autobiography.getAutobiographyStatus().updateStatusType(
                newStatus,
                LocalDateTime.now()
        );
        log.info("[PATCH_AUTOBIOGRAPHY_STATUS] 자서전 상태 변경 완료 - autobiographyId: {}, newStatus: {}", autobiographyId, newStatus);
    }

    public void deleteAutobiography(Long memberId, Long autobiographyId) {
		log.info("[DELETE_AUTOBIOGRAPHY] 자서전 삭제 시작 - memberId: {}, autobiographyId: {}", memberId, autobiographyId);
		
		Autobiography autobiography = autobiographyRepository.findById(autobiographyId)
				.orElseThrow(AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND::toServiceException);
		
		if (!autobiography.getMember().getId().equals(memberId)) {
			log.warn("[DELETE_AUTOBIOGRAPHY] 자서전 소유자 불일치 - memberId: {}, ownerId: {}", memberId, autobiography.getMember().getId());
			throw AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_OWNER.toServiceException();
		}
		
		autobiographyRepository.delete(autobiography);
		log.info("[DELETE_AUTOBIOGRAPHY] 자서전 삭제 완료 - autobiographyId: {}", autobiographyId);
	}

    // ------------------------------------------------------------------
    // CoShow용 서비스 메서드
    @Async
    public void coShowRequestAutobiographyGenerate(Long autobiographyId, CoShowAutobiographyGenerateRequestDto requestDto) {
        log.info("[COSHOW_REQUEST_AUTOBIOGRAPHY_GENERATE] CoShow 자서전 생성 요청 시작 (비동기) - autobiographyId: {}, name: {}", autobiographyId, requestDto.getName());

        Autobiography autobiography = autobiographyRepository.findById(autobiographyId)
                .orElseThrow(AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND::toServiceException);

        log.info("[COSHOW_REQUEST_AUTOBIOGRAPHY_GENERATE] 자서전 조회 완료 - autobiographyId: {}", autobiographyId);

        // 자서전 생성 요청
        log.info("[COSHOW_REQUEST_AUTOBIOGRAPHY_GENERATE] 자서전 생성 큐 전송 시작 - autobiographyId: {}", autobiographyId);
        autobiographyCompletionService.coShowTriggerPublicationRequest(autobiography, requestDto.getName());
        log.info("[COSHOW_REQUEST_AUTOBIOGRAPHY_GENERATE] CoShow 자서전 생성 요청 완료 - autobiographyId: {}", autobiographyId);
    }

    public void coShowNewRequestAutobiographyGenerate(Long autobiographyId, CoShowAutobiographyGenerateRequestDto requestDto) {
        log.info("[COSHOW_REQUEST_AUTOBIOGRAPHY_GENERATE] CoShow 자서전 생성 요청 시작 (동기) - autobiographyId: {}", autobiographyId);

        Autobiography autobiography = autobiographyRepository.findById(autobiographyId)
                .orElseThrow(AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND::toServiceException);

        log.info("[COSHOW_REQUEST_AUTOBIOGRAPHY_GENERATE] 자서전 조회 완료 - autobiographyId: {}", autobiographyId);

        // 자서전 생성 요청
        log.info("[COSHOW_REQUEST_AUTOBIOGRAPHY_GENERATE] 자서전 생성 큐 전송 시작 - autobiographyId: {}", autobiographyId);
        autobiographyCompletionService.coShowTriggerPublicationRequest(autobiography, requestDto.getName());
        log.info("[COSHOW_REQUEST_AUTOBIOGRAPHY_GENERATE] CoShow 자서전 생성 요청 완료 - autobiographyId: {}", autobiographyId);
    }


    // CoShow용 - 자서전 초기화
    public AutobiographyInitResponseDto coShowInitAutobiography(AutobiographyInitRequestDto requestDto) {
        log.info("[COSHOW_INIT_AUTOBIOGRAPHY] CoShow 자서전 초기화 시작 - theme: {}", requestDto.getTheme());
        
        LocalDateTime now = LocalDateTime.now();

        String tempEmail = "coshow1234@test.com";

        // 임시 회원 조회 또는 생성
        Member member = memberRepository.findByEmail(tempEmail)
                .orElseGet(() -> {
                    log.warn("[COSHOW_INIT_AUTOBIOGRAPHY] 임시 회원 미존재 - email: {}, 신규 생성 시작", tempEmail);
                    PasswordMember passwordMember = PasswordMember.of("coshow1234");
                    passwordMemberRepository.save(passwordMember);
                    log.info("[REGISTER_EMAIL] PasswordMember 저장 완료 - id: {}", passwordMember.getId());

                    Member newMember = Member.of(
                            LoginType.PASSWORD,
                            tempEmail,
                            MemberRole.MEMBER,
                            null,
                            tempEmail,
                            now,
                            now,
                            null
                    );
                    newMember.addPasswordMember(passwordMember);
                    memberRepository.save(newMember);

                    MemberMetadata matadata = MemberMetadata.ofV2(
                            "coshow",
                            GenderType.FEMALE,
                            "30",
                            "학생",
                            newMember
                    );

                    memberMetadataRepository.save(matadata);

                    return newMember;
                });

        log.info("[COSHOW_INIT_AUTOBIOGRAPHY] 임시 회원 조회 완료 - memberId: {}", member.getId());

        if (requestDto.getReason() != null && requestDto.getReason().length() > 500) {
            log.warn("[COSHOW_INIT_AUTOBIOGRAPHY] 자서전 이유 길이 초과 - length: {}", requestDto.getReason().length());
            throw AutobiographyExceptionStatus.AUTOBIOGRAPHY_REASON_LENGTH_EXCEEDED.toServiceException();
        }

        Autobiography autobiography = Autobiography.ofV2(
                null,
                null,
                null,

                requestDto.getTheme(),
                requestDto.getReason(),
                now,
                now,
                member
        );
        // save init autobiography
        Autobiography savedAutobiography = autobiographyRepository.save(autobiography);
        log.info("[COSHOW_INIT_AUTOBIOGRAPHY] 자서전 저장 완료 - autobiographyId: {}", savedAutobiography.getId());

        AutobiographyStatus autobiographyStatus = AutobiographyStatus.of(
                AutobiographyStatusType.PROGRESSING,
                member,
                autobiography,
                now
        );

        autobiographyStatusRepository.save(autobiographyStatus);
        log.info("[COSHOW_INIT_AUTOBIOGRAPHY] 자서전 상태 저장 완료 - status: PROGRESSING");

        // AI 데이터 기반으로 분류 체계 초기화
        // classificationInitService.initializeFromAiData(autobiography);
        // log.info("[COSHOW_INIT_AUTOBIOGRAPHY] 분류 체계 초기화 완료 - autobiographyId: {}", savedAutobiography.getId());

        // save init interview
        Interview interview = Interview.ofV2(
                now,
                autobiography,
                member,
                null
        );

        Interview savedInterview = interviewRepository.save(interview);
        log.info("[COSHOW_INIT_AUTOBIOGRAPHY] 인터뷰 저장 완료 - interviewId: {}", savedInterview.getId());

        // interview question을 dummy로 5개 주입
        log.info("[COSHOW_INIT_AUTOBIOGRAPHY] 인터뷰 저장 완료 - interviewId: {}", savedInterview.getId());

        // theme 읽기
        String theme = requestDto.getTheme();

        // theme별 override
        List<String> questions = THEME_QUESTIONS.get(theme);
        List<String> materials = THEME_MATERIALS.get(theme);

        if (questions == null || materials == null) {
            log.error("[COSHOW_INIT_AUTOBIOGRAPHY] 테마에 해당하는 질문 또는 자료가 없음 - theme: {}", theme);
            throw AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND.toServiceException();
        }

        log.info("[COSHOW_INIT_AUTOBIOGRAPHY] 더미 질문 생성 시작 - theme: {}, count: {}", theme, questions.size());

        // 질문 생성
        for (int i = 1; i <= questions.size(); i++) {
            String questionText = questions.get(i - 1);
            String materialText = materials.get(i - 1);

            InterviewQuestion interviewQuestion = InterviewQuestion.ofV2(
                    i,
                    questionText,
                    materialText,
                    now,
                    savedInterview
            );

            interviewQuestionRepository.save(interviewQuestion);
        }

        log.info("[COSHOW_INIT_AUTOBIOGRAPHY] 더미 질문 생성 완료 - 최종 생성된 질문 개수 : {}",
                interviewQuestionRepository.findAllByInterviewId(savedInterview.getId()).size());

        InterviewQuestion firstQuestion = interviewQuestionRepository.findByInterviewIdAndQuestionOrder(
                savedInterview.getId(),
                1
        ).orElseThrow(() -> {
            log.error("[COSHOW_INIT_AUTOBIOGRAPHY] 첫 번째 질문 조회 실패 - interviewId: {}, questionOrder: 1", savedInterview.getId());
            return InterviewExceptionStatus.INTERVIEW_QUESTION_NOT_FOUND.toServiceException();
        });

        Conversation conversation = Conversation.ofV2(
                firstQuestion.getQuestionText(),
                ConversationType.BOT,
                firstQuestion.getMaterials(),
                savedInterview,
                now
        );

        conversationRepository.save(conversation);
        log.info("[COSHOW_INIT_AUTOBIOGRAPHY] 첫 번째 대화 저장 완료 - conversationId: {}", conversation.getId());

        // current question 세팅
        savedInterview.setCurrentQuestion(firstQuestion);
        log.info("[COSHOW_INIT_AUTOBIOGRAPHY] 현재 질문 설정 완료 - questionId: {}", firstQuestion.getId());

        log.info("[COSHOW_INIT_AUTOBIOGRAPHY] CoShow 자서전 초기화 완료 - autobiographyId: {}, interviewId: {}", 
                savedAutobiography.getId(), savedInterview.getId());

        return AutobiographyInitResponseDto.builder()
                .autobiographyId(savedAutobiography.getId())
                .interviewId(savedInterview.getId())
                .build();
    }

    // CoShow용 - 더미 질문 리스트 반환
    private static final Map<String, List<String>> THEME_QUESTIONS = Map.of(
            "love", List.of(
                    "배우자와 함께 지내면서 따뜻함을 느꼈던 순간이 있다면 언제인가요?",
                    "그때 배우자가 했던 행동이나 말 중에 특히 기억에 남았던 내용을 알려 주세요.",
                    "그 순간이 지금의 당신에게 어떤 의미나 힘이 되었나요?",

                    "결혼 생활 중에서 ‘전환점’처럼 느껴졌던 시기가 있다면 언제였나요?",
                    "그 시기에 있었던 일 중 기억에 남는 장면이나 대화를 하나 말해 주세요.",
                    "그 경험이 결혼 생활을 바라보는 당신의 생각이나 태도에 어떤 변화를 주었나요?"
            ),

            "friend", List.of(
                    "함께 일했던 사람 중, 유난히 인상적이었던 동료가 있다면 어떤 사람이었나요?",
                    "그 동료와 관련해 기억에 남는 장면이나 순간을 말해 주세요.",
                    "그 경험이 당신의 일하는 방식이나 사람을 대하는 태도에 어떤 영향을 주었나요?",

                    "학창시절이나 어린 시절의 친구와 관련해 떠오르는 기억이 있나요?",
                    "그 친구와 있었던 즐거운 장면 하나를 떠올려 말해 주세요.",
                    "그 경험이 지금의 당신에게 어떤 의미나 교훈을 남겼는지 말해 주세요."
            ),

            "hobby", List.of(
                    "요즘 당신을 가장 편하게 해주는 취미나 활동은 무엇인가요?",
                    "그 활동을 할 때 특히 마음이 가는 순간이나 패턴을 떠올려 말해 주세요.",
                    "그 취미가 요즘 당신의 하루나 생활에 어떤 변화를 주었나요?",

                    "과거에 빠져 있던 취미나 활동 중 기억에 남는 것이 있나요?",
                    "그 취미를 즐기던 시절 떠오르는 장면이나 분위기를 말해 주세요.",
                    "지금 돌아보면, 그 취미가 당신에게 어떤 성향이나 습관을 남긴 것 같나요?"
            ),

            "pet", List.of(
                    "지금 혹은 예전에 함께 지낸 반려동물이 있다면, 그 아이는 어떤 성격이나 특징을 가진 아이였나요?",
                    "그 아이와 함께 지내며 유난히 마음에 남았던 행동이나 순간을 떠올려 말해 주세요.",
                    "그 순간이 당신에게 어떤 감정을 주었나요?",

                    "반려동물과 함께한 일상 중, 지금 떠올리면 즐겁거나 편안해지는 장면이 있다면 어떤 모습인가요?",
                    "그때의 분위기나 하루의 흐름 속에서 기억나는 구체적인 순간을 들려주세요.",
                    "그 경험이 당신의 하루나 생활 방식에 어떤 변화를 준 것 같나요?"
            ),

            "philosophy", List.of(
                    "요즘 당신이 중요하게 생각하는 가치나 태도는 무엇인가요?",
                    "그 생각을 하게 된 계기나 하루의 장면을 떠올려 말해 주세요.",
                    "그 경험이 당신에게 어떤 의미를 줬는지 설명해 주세요.",

                    "과거에 당신의 생각을 바꿔 놓았던 사건이 있다면 언제였나요?",
                    "그 순간 있었던 구체적 장면이나 대화를 알려주세요.",
                    "그 경험이 현재의 당신을 어떻게 형성했는지 말해 주세요."
            ),

            "career", List.of(
                    "지금의 일을 시작하게 된 계기나, 처음 진로를 선택할 때의 생각을 말해 주세요.",
                    "진로를 결정하거나 바꾸는 과정에서 기억에 남는 순간이나 고민을 들려주세요.",
                    "그 경험이 지금의 일하는 방식이나 당신의 커리어 방향에 어떤 의미를 남겼나요?",

                    "커리어 중에서 ‘전환점’처럼 느껴졌던 순간이 있다면 언제였나요?",
                    "그때 있었던 상황이나 선택을 하나 구체적으로 말해 주세요.",
                    "그 경험이 지금의 일하는 방식이나 가치관에 어떤 영향을 주었나요?"
            ),

            "growing", List.of(
                    "어릴 때의 당신은 어떤 성격이었던 것 같나요? 조용한 편이었는지, 활발했는지 등 기억나는 모습이 있다면 말해 주세요.",
                    "어린 시절 성격을 잘 보여주는 장면이나 상황을 하나 떠올려 말해 주세요.",
                    "지금 돌아보면, 그때의 성격이 지금의 당신에게 어떤 영향을 준 것 같나요?",

                    "어린 시절에 함께 시간을 많이 보냈던 사람이나 친구가 있었나요?",
                    "그 사람과 함께한 기억 중 떠오르는 장면 하나를 말해 주세요.",
                    "그 경험이 당신에게 어떤 따뜻함이나 영향을 남겼는지 말해 주세요."
            )
    );

    // CoShow용 - 더미 자료 리스트 반환
    private static final Map<String, List<String>> THEME_MATERIALS = Map.of(
            "love", List.of(
                    "[[10,1,5]]", "[[10,1,5]]", "[[10,1,5]]","[[9,3,1]]", "[[9,3,1]]", "[[9,3,1]]"
            ),
            "friend", List.of(
                    "[[15,2,2]]", "[[15,2,2]]", "[[15,2,2]]","[[11,3,5]]", "[[11,3,5]]", "[[11,3,5]]"
            ),
            "hobby", List.of(
                    "[[21,2,2]]", "[[21,2,2]]", "[[21,2,2]]","[[18,3,5]]", "[[18,3,5]]", "[[18,3,5]]"
            ),
            "pet", List.of(
                    "[[13,2,1]]", "[[13,2,1]]", "[[13,2,1]]","[[21,3,1]]", "[[21,3,1]]", "[[21,3,1]]"
            ),
            "philosophy", List.of(
                    "[[20,11,1]]", "[[20,11,1]]", "[[20,11,1]]","[[8,14,1]]", "[[8,14,1]]", "[[8,14,1]]"
            ),
            "career", List.of(
                    "[[16,1,2]]", "[[16,1,2]]", "[[16,1,2]]","[[15,1,1]]", "[[15,1,1]]", "[[15,1,1]]"
            ),
            "growing", List.of(
                    "[[8,1,2]]", "[[8,1,2]]", "[[8,1,2]]","[[14,1,1]]", "[[14,1,1]]", "[[14,1,1]]"
            )

    );
}
