package com.lifelibrarians.lifebookshelf.event;

import com.lifelibrarians.lifebookshelf.publication.domain.PublicationManager;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PublicationCreatedEventListener {

	private final PublicationManager publicationManager;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handlePublicationCreatedEvent(PublicationCreatedEvent event) {
		publicationManager.invokeNewPublicationProcessor(event.getPublicationId());
	}
}
