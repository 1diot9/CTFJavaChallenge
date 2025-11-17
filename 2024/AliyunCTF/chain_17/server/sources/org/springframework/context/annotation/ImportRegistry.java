package org.springframework.context.annotation;

import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/ImportRegistry.class */
interface ImportRegistry {
    @Nullable
    AnnotationMetadata getImportingClassFor(String importedClass);

    void removeImportingClass(String importingClass);
}
