package com.lifelibrarians.lifebookshelf.app.domain;

import java.util.Arrays;
import java.util.Optional;

public enum PlatformType {
    ANDROID,
    IOS;

    public static Optional<PlatformType> from(String value) {
        return Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(value))
                .findFirst();
    }
}
