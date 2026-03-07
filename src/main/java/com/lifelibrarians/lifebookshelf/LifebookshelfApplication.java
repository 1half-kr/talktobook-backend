package com.lifelibrarians.lifebookshelf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class LifebookshelfApplication {

	public static void main(final String[] args) {
		System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true");
		SpringApplication.run(LifebookshelfApplication.class, args);
	}

}
