package com.lifelibrarians.lifebookshelf.publication.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Profile("!prod")
public class NonProdPublicationManager implements PublicationManager {

	@Override
	public void invokeNewPublicationProcessor(Long publicationId) {
		log.info("Non-production environment. Skipping Lambda invocation for publicationId: {}",
				publicationId);
	}
}
