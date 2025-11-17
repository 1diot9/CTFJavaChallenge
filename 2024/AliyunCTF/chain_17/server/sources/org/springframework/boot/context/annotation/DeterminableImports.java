package org.springframework.boot.context.annotation;

import java.util.Set;
import org.springframework.core.type.AnnotationMetadata;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/annotation/DeterminableImports.class */
public interface DeterminableImports {
    Set<Object> determineImports(AnnotationMetadata metadata);
}
