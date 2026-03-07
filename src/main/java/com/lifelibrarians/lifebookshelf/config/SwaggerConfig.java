package com.lifelibrarians.lifebookshelf.config;

import static java.util.stream.Collectors.groupingBy;

import com.lifelibrarians.lifebookshelf.exception.annotation.ApiErrorCodeExample;
import com.lifelibrarians.lifebookshelf.exception.status.ErrorReason;
import com.lifelibrarians.lifebookshelf.exception.status.ExceptionStatus;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

@Configuration
@OpenAPIDefinition(
		info = @Info(title = "Talktobook API", version = "v1"),
		security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
		name = "bearerAuth",
		type = SecuritySchemeType.HTTP,
		scheme = "bearer"
)
public class SwaggerConfig {

    @Value("${security.cors.domain}")
    private String corsDomain;

    @Value("${security.cors.ip}")
    private String corsIp;

	@Bean
	public OpenAPI getOpenAPI() {
		return new OpenAPI()
				.components(new Components()
						.addHeaders("Authorization",
								new Header().description("Auth header")
										.schema(new StringSchema()))
				)
				.servers(List.of(
						new io.swagger.v3.oas.models.servers.Server()
								.url("http://localhost:8080")
								.description("Local Server"),
						new io.swagger.v3.oas.models.servers.Server()
								.url(corsIp)
								.description("Dev Server"),
						new io.swagger.v3.oas.models.servers.Server()
								.url(corsDomain)
								.description("Production Server")
				));
	}

	/**
	 * 커스텀 오퍼레이션을 Bean에 등록합니다.
	 *
	 * @return OperationCustomizer
	 */
	@Bean
	public OperationCustomizer applyCustomResponse() {
		return (Operation operation, HandlerMethod handlerMethod) -> {
			ApiErrorCodeExample apiErrorCodeExample =
					handlerMethod.getMethodAnnotation(ApiErrorCodeExample.class);
			if (apiErrorCodeExample != null) {
				generateErrorCodeResponseExample(operation, apiErrorCodeExample);
			}
			return operation;
		};
	}

	/**
	 * ${@link ApiErrorCodeExample} 의 정보를 바탕으로 Swagger 예시 응답을 생성합니다.
	 *
	 * @param operation           Swagger API 오퍼레이션 정보
	 * @param apiErrorCodeExample ApiErrorCodeExample
	 */
	private void generateErrorCodeResponseExample(
			Operation operation,
			ApiErrorCodeExample apiErrorCodeExample
	) {
		ApiResponses responses = operation.getResponses();

		List<ExceptionStatus> exceptionStatuses = toExceptionStatusList(
				apiErrorCodeExample);

		Map<Integer, List<SwaggerExampleHolder>> statusWithExampleHolders =
				generateStatusWithExampleHolders(exceptionStatuses);

		addExamplesToResponses(responses, statusWithExampleHolders);
	}

	/**
	 * ${@link ApiErrorCodeExample} 의 ${@link ExceptionStatus} 들을 리스트로 변환합니다. 도메인 별로 분리된
	 * ExceptionStatus 들을 하나의 리스트로 합칩니다.
	 *
	 * @param apiErrorCodeExample ApiErrorCodeExample
	 * @return ExceptionStatus 리스트
	 */
	private List<ExceptionStatus> toExceptionStatusList(
			ApiErrorCodeExample apiErrorCodeExample) {
		List<ExceptionStatus> exceptionStatusList = new ArrayList<>();
		exceptionStatusList.addAll(
				Arrays.asList(apiErrorCodeExample.authExceptionStatuses()));
		exceptionStatusList.addAll(
				Arrays.asList(apiErrorCodeExample.autobiographyExceptionStatuses()));
		exceptionStatusList.addAll(Arrays.asList(apiErrorCodeExample.interviewExceptionStatuses()));
		exceptionStatusList.addAll(Arrays.asList(apiErrorCodeExample.memberExceptionStatuses()));
		exceptionStatusList.addAll(
				Arrays.asList(apiErrorCodeExample.publicationExceptionStatuses()));
		exceptionStatusList.addAll(Arrays.asList(apiErrorCodeExample.communityExceptionStatuses()));
		exceptionStatusList.addAll(Arrays.asList(apiErrorCodeExample.utilsExceptionStatuses()));
		return exceptionStatusList;
	}

	/**
	 * ${@link ExceptionStatus} 들을 ${@link SwaggerExampleHolder} 로 변환합니다.
	 *
	 * @param exceptionStatuses ExceptionStatus 리스트
	 * @return SwaggerExampleHolder 리스트
	 */
	private Map<Integer, List<SwaggerExampleHolder>> generateStatusWithExampleHolders(
			List<ExceptionStatus> exceptionStatuses
	) {
		return
				exceptionStatuses.stream()
						.map(
								exceptionStatus -> {
									try {
										ErrorReason errorReason = exceptionStatus.getErrorReason();
										return SwaggerExampleHolder.builder()
												.holder(
														getSwaggerExample(
																errorReason))
												.code(errorReason.getStatusCode())
												.name(errorReason.getMessage())
												.build();
									} catch (Exception e) {
										throw new RuntimeException(e);
									}
								})
						.collect(groupingBy(SwaggerExampleHolder::getCode));
	}

	/**
	 * errorReason을 바탕으로 Swagger Example 객체를 생성합니다.
	 *
	 * @param errorReason ${@link ErrorReason} 에러가 발생한 사유
	 * @return Swagger Example ${@link Example} Swagger Example 객체
	 */
	private Example getSwaggerExample(ErrorReason errorReason) {
		Example example = new Example();
		example.setValue(errorReason);
		return example;
	}

	/**
	 * ${@link ApiResponses} 에 예시 응답을 추가합니다.
	 *
	 * @param responses                Swagger API 응답 정보
	 * @param statusWithExampleHolders 예시 응답을 담고 있는 Map
	 */
	private void addExamplesToResponses(
			ApiResponses responses,
			Map<Integer, List<SwaggerExampleHolder>> statusWithExampleHolders) {
		statusWithExampleHolders.forEach(
				(status, v) -> {
					Content content = new Content();
					MediaType mediaType = new MediaType();
					ApiResponse apiResponse = new ApiResponse();
					v.forEach(
							exampleHolder -> mediaType.addExamples(
									exampleHolder.getName(), exampleHolder.getHolder()));
					content.addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
							mediaType);
					apiResponse.setContent(content);
					responses.addApiResponse(status.toString(), apiResponse);
				});
	}
}
