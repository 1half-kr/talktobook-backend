package com.lifelibrarians.lifebookshelf.notification.controller;

import com.lifelibrarians.lifebookshelf.auth.dto.MemberSessionDto;
import com.lifelibrarians.lifebookshelf.auth.jwt.LoginMemberInfo;
import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.notification.dto.request.NotificationHistoryDeleteRequestDto;
import com.lifelibrarians.lifebookshelf.notification.dto.request.NotificationHistoryReadRequestDto;
import com.lifelibrarians.lifebookshelf.notification.dto.request.SubscribingNotificationUpdateRequestDto;
import com.lifelibrarians.lifebookshelf.notification.dto.response.NotificationHistoryListResponseDto;
import com.lifelibrarians.lifebookshelf.notification.dto.response.SubscribingNotificationListResponseDto;
import com.lifelibrarians.lifebookshelf.notification.service.NotificationFacadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/notifications")
@Tag(name = "알림 (Notification)", description = "알림 관련 API")
@Logging
public class NotificationController {

	private final NotificationFacadeService notificationFacadeService;

	@Operation(summary = "구독중인 알림 목록 조회", description = "구독중인 알림 목록을 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
	})
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/subscriptions")
	public SubscribingNotificationListResponseDto getSubscriptions(
			@LoginMemberInfo MemberSessionDto memberSessionDto
	) {
		return notificationFacadeService.getSubscriptions(memberSessionDto.getMemberId());
	}

	@Operation(summary = "알림 구독 변경", description = "알림 구독을 변경합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
	})
	@PreAuthorize("isAuthenticated()")
	@PutMapping("/subscriptions")
	public SubscribingNotificationListResponseDto updateSubscriptions(
			@LoginMemberInfo MemberSessionDto memberSessionDto,
			@Valid @RequestBody SubscribingNotificationUpdateRequestDto requestDto
	) {
		return notificationFacadeService.updateSubscriptions(memberSessionDto.getMemberId(),
				requestDto);
	}

	@Operation(summary = "알림 내역 목록 조회", description = "알림 내역 목록을 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
	})
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/histories")
	public NotificationHistoryListResponseDto getHistories(
			@LoginMemberInfo MemberSessionDto memberSessionDto,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size
	) {
		return notificationFacadeService.getHistories(memberSessionDto.getMemberId(),
				PageRequest.of(page, size));
	}

	@Operation(summary = "알림 내역 읽음 처리", description = "알림 내역을 읽음 처리합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
	})
	@PreAuthorize("isAuthenticated()")
	@PutMapping("/histories")
	public void updateHistories(
			@LoginMemberInfo MemberSessionDto memberSessionDto,
			@Valid @RequestBody NotificationHistoryReadRequestDto requestDto
	) {
		notificationFacadeService.readHistories(memberSessionDto.getMemberId(), requestDto);
	}

	@Operation(summary = "알림 내역 삭제", description = "알림 내역을 삭제합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
	})
	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("/histories")
	public void deleteHistories(
			@LoginMemberInfo MemberSessionDto memberSessionDto,
			@Valid @RequestBody NotificationHistoryDeleteRequestDto requestDto
	) {
		notificationFacadeService.deleteHistories(memberSessionDto.getMemberId(), requestDto);
	}
}
