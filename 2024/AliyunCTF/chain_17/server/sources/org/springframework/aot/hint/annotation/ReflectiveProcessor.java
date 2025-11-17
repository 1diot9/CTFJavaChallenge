package org.springframework.aot.hint.annotation;

import java.lang.reflect.AnnotatedElement;
import org.springframework.aot.hint.ReflectionHints;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/annotation/ReflectiveProcessor.class */
public interface ReflectiveProcessor {
    void registerReflectionHints(ReflectionHints hints, AnnotatedElement element);
}
