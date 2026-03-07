package com.lifelibrarians.lifebookshelf.auth.dto;

import com.lifelibrarians.lifebookshelf.member.domain.MemberRole;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;


@Getter
@AllArgsConstructor
@Builder
@ToString
@FieldNameConstants
public class MemberSessionDto {

	private final Long memberId;
	private final List<MemberRole> roles;
}
