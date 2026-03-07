package com.lifelibrarians.lifebookshelf.publication.domain;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lifelibrarians.lifebookshelf.exception.status.PublicationExceptionStatus;
import com.lifelibrarians.lifebookshelf.log.Logging;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Logging
@Slf4j
@Profile("prod")
public class ProdPublicationManager implements PublicationManager {

	private final AWSLambda awsLambda;
	private final ObjectMapper objectMapper;

	public void invokeNewPublicationProcessor(Long publicationId) {
		String functionName = "NewPublicationProcessor";

		try {

			ObjectNode payloadNode = objectMapper.createObjectNode();
			ObjectNode bodyNode = objectMapper.createObjectNode();
			bodyNode.put("publicationId", publicationId);
			payloadNode.set("body", bodyNode);

			String payload = objectMapper.valueToTree(payloadNode).toString();

			System.out.println("Payload: " + payload);

			InvokeRequest invokeRequest = new InvokeRequest()
					.withFunctionName(functionName)
					.withPayload(payload);

			InvokeResult invokeResult = awsLambda.invoke(invokeRequest);
			String ans = new String(invokeResult.getPayload().array(), StandardCharsets.UTF_8);
			JsonNode jsonResponse = objectMapper.readTree(ans);
			if (
					!jsonResponse.has("statusCode") ||
							(jsonResponse.has("statusCode") && jsonResponse.get("statusCode").asInt() != 200)
			) {
				log.error("Lambda function returned: {}", ans);
				throw PublicationExceptionStatus.PUBLICATION_PROCESSOR_FAILED.toDomainException();
			}
			log.info("Lambda function returned: {}", ans);
		} catch (Exception e) {
			log.error("Error invoking lambda function: {}", e.getMessage());
			throw PublicationExceptionStatus.PUBLICATION_PROCESSOR_FAILED.toDomainException();
		}
	}
}