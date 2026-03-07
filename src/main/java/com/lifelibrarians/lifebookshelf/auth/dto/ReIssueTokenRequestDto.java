package com.lifelibrarians.lifebookshelf.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;

@Getter
@Builder
@FieldNameConstants
public class ReIssueTokenRequestDto {
    private String refreshToken;
}
