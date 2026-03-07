package com.lifelibrarians.lifebookshelf.event;

public class PublicationCreatedEvent {

	private final Long publicationId;

	public PublicationCreatedEvent(Long publicationId) {
		this.publicationId = publicationId;
	}

	public Long getPublicationId() {
		return publicationId;
	}
}
