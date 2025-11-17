package org.springframework.aot.generate;

import org.springframework.aot.hint.RuntimeHints;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/generate/GenerationContext.class */
public interface GenerationContext {
    GeneratedClasses getGeneratedClasses();

    GeneratedFiles getGeneratedFiles();

    RuntimeHints getRuntimeHints();

    GenerationContext withName(String name);
}
