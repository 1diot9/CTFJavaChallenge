package org.springframework.boot.autoconfigure.security;

import java.util.Arrays;
import java.util.stream.Stream;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/StaticResourceLocation.class */
public enum StaticResourceLocation {
    CSS("/css/**"),
    JAVA_SCRIPT("/js/**"),
    IMAGES("/images/**"),
    WEB_JARS("/webjars/**"),
    FAVICON("/favicon.*", "/*/icon-*");

    private final String[] patterns;

    StaticResourceLocation(String... patterns) {
        this.patterns = patterns;
    }

    public Stream<String> getPatterns() {
        return Arrays.stream(this.patterns);
    }
}
