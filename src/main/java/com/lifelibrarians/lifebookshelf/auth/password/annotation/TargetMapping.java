package com.lifelibrarians.lifebookshelf.auth.password.annotation;

public @interface TargetMapping {

	Class<?> clazz();

	String[] fields() default {};
}
