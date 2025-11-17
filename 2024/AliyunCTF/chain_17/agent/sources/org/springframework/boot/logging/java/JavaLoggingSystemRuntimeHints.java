package org.springframework.boot.logging.java;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/java/JavaLoggingSystemRuntimeHints.class */
class JavaLoggingSystemRuntimeHints implements RuntimeHintsRegistrar {
    JavaLoggingSystemRuntimeHints() {
    }

    @Override // org.springframework.aot.hint.RuntimeHintsRegistrar
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.resources().registerPattern("org/springframework/boot/logging/java/logging.properties");
        hints.resources().registerPattern("org/springframework/boot/logging/java/logging-file.properties");
    }
}
