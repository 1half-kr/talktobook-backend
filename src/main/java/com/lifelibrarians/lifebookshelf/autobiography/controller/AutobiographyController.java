package com.lifelibrarians.lifebookshelf.autobiography.controller;

import com.lifelibrarians.lifebookshelf.auth.dto.MemberSessionDto;
import com.lifelibrarians.lifebookshelf.auth.jwt.LoginMemberInfo;
import com.lifelibrarians.lifebookshelf.autobiography.dto.request.AutobiographyInitRequestDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.request.AutobiographyUpdateRequestDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.request.CoShowAutobiographyGenerateRequestDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.response.*;
import com.lifelibrarians.lifebookshelf.autobiography.service.AutobiographyFacadeService;
import com.lifelibrarians.lifebookshelf.exception.status.AutobiographyExceptionStatus;
import com.lifelibrarians.lifebookshelf.exception.annotation.ApiErrorCodeExample;
import com.lifelibrarians.lifebookshelf.log.Logging;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v2/autobiographies")
@Tag(name = "자서전 (Autobiography)", description = "자서전 관련 API")
@Logging
public class AutobiographyController {

	private final AutobiographyFacadeService autobiographyFacadeService;

    @Operation(summary = "테마와 자서전 생성 이유 등록 요청", description = "온보딩과 자서전을 최초 생성하는 시점에서, 자서전에 대한 테마와 생성 이유를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "created"),
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/init", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public AutobiographyInitResponseDto initAutobiography(
            @LoginMemberInfo MemberSessionDto memberSessionDto,
            @Valid @ModelAttribute AutobiographyInitRequestDto requestDto
    ) {
        return autobiographyFacadeService.initAutobiography(memberSessionDto.getMemberId(), requestDto);
    }

	@Operation(summary = "자서전 목록 조회", description = "유저가 보유한 전체 자서전 목록을 pagination으로 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
	})
	@PreAuthorize("isAuthenticated()")
	@GetMapping
    @ResponseStatus(HttpStatus.OK)
	public AutobiographyListResponseDto getAutobiographies(
			@LoginMemberInfo MemberSessionDto memberSessionDto,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "statuses", defaultValue = "EMPTY,PROGRESSING,ENOUGH,CREATING,FINISH", required = false) List<String> statuses // ex.: ["PROGRESSING", "ENOUGH", "COMPLETED"]
	) {
		return autobiographyFacadeService.getAutobiographies(memberSessionDto.getMemberId(), statuses, PageRequest.of(page, size));
	}

    @Operation(summary = "특정 자서전에서 count된 소재를 오름차순으로 반환", description = "auto id에 맞는 자서전에서 count 된 모든 소재에서 가 해당 자서전에서 count 된 소재를 count(가중치) 기준 내림차순으로 반환한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ok"),
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{autobiographyId}/materials")
    @ResponseStatus(HttpStatus.OK)
    public AutobiographyMaterialsResponseDto getAutobiographyMaterials(
            @LoginMemberInfo MemberSessionDto memberSessionDto,
            @PathVariable("autobiographyId") @Parameter(description = "자서전 ID") Long autobiographyId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "asc", required = false) String sort
    ) {
        return autobiographyFacadeService.getAutobiographyMaterials(memberSessionDto.getMemberId(), autobiographyId, sort, PageRequest.of(page, size));
    }


    @Operation(summary = "특정 자서전 상세 조회", description = "자서전 id로 자서전의 상세 정보를 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
	})
	@ApiErrorCodeExample(
			autobiographyExceptionStatuses = {
					AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND,
					AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_OWNER
			}
	)
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{autobiographyId}")
    @ResponseStatus(HttpStatus.OK)
	public AutobiographyDetailResponseDto getAutobiography(
			@LoginMemberInfo MemberSessionDto memberSessionDto,
			@PathVariable("autobiographyId") @Parameter(description = "자서전 ID") Long autobiographyId
	) {
		return autobiographyFacadeService.getAutobiography(memberSessionDto.getMemberId(), autobiographyId);
	}

	@Operation(summary = "특정 자서전 수정 요청", description = "자서전 id로 자서전의 title, content, image를 수정합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
	})
	@ApiErrorCodeExample(
			autobiographyExceptionStatuses = {
					AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND,
					AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_OWNER,
					AutobiographyExceptionStatus.AUTOBIOGRAPHY_TITLE_LENGTH_EXCEEDED,
					AutobiographyExceptionStatus.AUTOBIOGRAPHY_CONTENT_LENGTH_EXCEEDED,
			}
	)
	@PreAuthorize("isAuthenticated()")
	@PostMapping(value = "/{autobiographyChapterId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public void updateAutobiography(
			@LoginMemberInfo MemberSessionDto memberSessionDto,
			@PathVariable("autobiographyChapterId") @Parameter(description = "자서전 챕터 ID") Long autobiographyChapterId,
			@Valid @ModelAttribute AutobiographyUpdateRequestDto requestDto
	) {
		autobiographyFacadeService.patchAutobiography(memberSessionDto.getMemberId(), autobiographyChapterId, requestDto);
	}

    @Operation(summary = "특정 자서전의 생성 이유 수정 요청", description = "자서전 id로 자서전의 reason를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ok"),
    })
    @ApiErrorCodeExample(
            autobiographyExceptionStatuses = {
                    AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND,
                    AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_OWNER,
                    AutobiographyExceptionStatus.AUTOBIOGRAPHY_REASON_LENGTH_EXCEEDED
            }
    )
    @PreAuthorize("isAuthenticated()")
    @PatchMapping(value = "/{autobiographyId}/reason", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void updateReasonAutobiography(
            @LoginMemberInfo MemberSessionDto memberSessionDto,
            @PathVariable("autobiographyId") @Parameter(description = "자서전 ID") Long autobiographyId,
            @Valid @ModelAttribute AutobiographyInitRequestDto requestDto
    ) {
        autobiographyFacadeService.patchReasonAutobiography(memberSessionDto.getMemberId(), autobiographyId, requestDto);
    }

    @Operation(summary = "현재 진행중인 자서전 생성 요청", description = "status가 ENOUGH인 경우에만 가능합니다. 특정 자서전의 status를 CREATING으로 변경하고 자서전 생성을 실행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ok"),
    })
    @ApiErrorCodeExample(
            autobiographyExceptionStatuses = {
                    AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND,
                    AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_OWNER
            }
    )
    @PreAuthorize("isAuthenticated()")
    @PatchMapping(value = "/{autobiographyId}/generate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void requestAutobiographyGenerate(
            @LoginMemberInfo MemberSessionDto memberSessionDto,
            @PathVariable("autobiographyId") @Parameter(description = "자서전 ID") Long autobiographyId,
            @Valid @ModelAttribute CoShowAutobiographyGenerateRequestDto requestDto
    ) {
        autobiographyFacadeService.requestAutobiographyGenerate(memberSessionDto.getMemberId(), autobiographyId, requestDto);
    }

    @Operation(summary = "현재 진행중인 자서전 상태를 변경", description = "특정 자서전의 상태를 변경 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ok"),
    })
    @ApiErrorCodeExample(
            autobiographyExceptionStatuses = {
                    AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND,
                    AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_OWNER
            }
    )
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{autobiographyId}/status")
    @ResponseStatus(HttpStatus.OK)
    public void patchAutobiographyReady(
            @LoginMemberInfo MemberSessionDto memberSessionDto,
            @PathVariable("autobiographyId") @Parameter(description = "자서전 ID") Long autobiographyId,
            @RequestParam(value = "status", defaultValue = "ENOUGH") String status
    ) {
        autobiographyFacadeService.patchAutobiographyStatus(memberSessionDto.getMemberId(), autobiographyId, status);
    }

    @Operation(summary = "현재 진행중인 자서전 id 조회", description = "자서전 상태가 PROGRESSING인 자서전 중 updatedAt이 가장 최신인 자서전의 id를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ok"),
    })
    @ApiErrorCodeExample(
            autobiographyExceptionStatuses = {
                    AutobiographyExceptionStatus.AUTOBIOGRAPHY_STATUS_NOT_FOUND,
                    AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_OWNER
            }
    )
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/current")
    public AutobiographyCurrentResponseDto getCurrentAutobiography(
            @LoginMemberInfo MemberSessionDto memberSessionDto
    ) {
        return autobiographyFacadeService.getCurrentAutobiography(memberSessionDto.getMemberId());
    }

	@Operation(summary = "특정 자서전 삭제 요청", description = "자서전 id로 자서전을 삭제합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
	})
	@ApiErrorCodeExample(
			autobiographyExceptionStatuses = {
					AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND,
					AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_OWNER
			}
	)
	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("/{autobiographyId}")
	public void deleteAutobiography(
			@LoginMemberInfo MemberSessionDto memberSessionDto,
			@PathVariable("autobiographyId") @Parameter(description = "자서전 ID") Long autobiographyId
	) {
		autobiographyFacadeService.deleteAutobiography(memberSessionDto.getMemberId(), autobiographyId);
	}

    @Operation(summary = "현재 진행 중인 자서전 인터뷰 진행률 조회", description = "auto id를 서버에서 찾아서 진행률 퍼센트를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ok"),
    })
    @ApiErrorCodeExample(
            autobiographyExceptionStatuses = {
                    AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND,
                    AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_OWNER
            }
    )
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{autobiographyId}/progress")
    public AutobiographyProgressResponseDto getAutobiographyProgress(
            @LoginMemberInfo MemberSessionDto memberSessionDto,
            @PathVariable("autobiographyId") @Parameter(description = "자서전 ID") Long autobiographyId
    ) {
        return autobiographyFacadeService.getAutobiographyProgress(memberSessionDto.getMemberId(), autobiographyId);
    }

    @Operation(summary = "선택한 자서전에 대한 테마 조회", description = "사용자가 선택한 auto id에 대한 theme 정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ok"),
    })
    @ApiErrorCodeExample(
            autobiographyExceptionStatuses = {
                    AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND,
                    AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_OWNER
            }
    )
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{autobiographyId}/theme")
    public AutobiographyThemeResponseDto getAutobiographyTheme(
            @LoginMemberInfo MemberSessionDto memberSessionDto,
            @PathVariable("autobiographyId") @Parameter(description = "자서전 ID") Long autobiographyId
    ) {
        return autobiographyFacadeService.getAutobiographyTheme(memberSessionDto.getMemberId(), autobiographyId);
    }

    // ----------------------------------------------------
    // CoShow용
    @Operation(summary = "4. CoShow - 최종 자서전 생성 요청 (New Version)", description = "CoShow용 - 자서전 ID로 최종 자서전 생성을 요청합니다. 상태가 CREATING으로 전환됩니다. 완성되면 상태가 FINISH로 전환되고, 곧 PDF로 확인할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "created"),
    })
    @ApiErrorCodeExample(
            autobiographyExceptionStatuses = {
                    AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND
            }
    )
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/{autobiographyId}/coshow/generate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void coShowNewRequestAutobiographyGenerate(
            @PathVariable("autobiographyId") @Parameter(description = "자서전 ID") Long autobiographyId,
            @Valid @ModelAttribute CoShowAutobiographyGenerateRequestDto requestDto
    ) {
        autobiographyFacadeService.coShowNewRequestAutobiographyGenerate(autobiographyId, requestDto);
    }

    @Operation(summary = "4. CoShow - 최종 자서전 생성 요청", description = "CoShow용 - 자서전 ID로 최종 자서전 생성을 요청합니다. 상태가 CREATING으로 전환됩니다. 완성되면 상태가 FINISH로 전환되고, 곧 PDF로 확인할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ok"),
    })
    @ApiErrorCodeExample(
            autobiographyExceptionStatuses = {
                    AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND
            }
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{autobiographyId}/coshow/generate")
    public void coShowRequestAutobiographyGenerate(
            @PathVariable("autobiographyId") @Parameter(description = "자서전 ID") Long autobiographyId,
            @Valid @ModelAttribute CoShowAutobiographyGenerateRequestDto requestDto
    ) {
        autobiographyFacadeService.coShowRequestAutobiographyGenerate(autobiographyId, requestDto);
    }

    @Operation(summary = "1. CoShow - 자서전 초기화 요청", description = "CoShow용 - 자서전 테마와 이유를 등록하고 새로운 자서전 id, 인터뷰 id를 반환합니다. 로그인이 필요하지 않습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "created"),
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/coshow/init", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AutobiographyInitResponseDto coShowInitAutobiography(
            @Valid @ModelAttribute AutobiographyInitRequestDto requestDto
    ) {
        return autobiographyFacadeService.coShowInitAutobiography(requestDto);
    }

    @Operation(summary = "5. CoShow - 현재 진행 중인 자서전 인터뷰 진행률 조회", description = "auto id를 서버에서 찾아서 진행률 퍼센트를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ok"),
    })
    @ApiErrorCodeExample(
            autobiographyExceptionStatuses = {
                    AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND
            }
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{autobiographyId}/coshow/progress")
    public AutobiographyProgressResponseDto coShowGetAutobiographyProgress(
            @PathVariable("autobiographyId") @Parameter(description = "자서전 ID") Long autobiographyId
    ) {
        return autobiographyFacadeService.coShowGetAutobiographyProgress(autobiographyId);
    }
}

