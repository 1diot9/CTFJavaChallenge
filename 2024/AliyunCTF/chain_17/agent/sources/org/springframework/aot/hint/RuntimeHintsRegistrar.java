package org.springframework.aot.hint;

import org.springframework.lang.Nullable;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/RuntimeHintsRegistrar.class */
public interface RuntimeHintsRegistrar {
    void registerHints(RuntimeHints hints, @Nullable ClassLoader classLoader);
}
