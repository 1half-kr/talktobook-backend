package com.lifelibrarians.lifebookshelf.image.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ImageDeleteEvent {

	private final String imageUrl;
}
