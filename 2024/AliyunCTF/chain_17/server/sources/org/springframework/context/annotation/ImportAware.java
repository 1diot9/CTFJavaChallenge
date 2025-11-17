package org.springframework.context.annotation;

import org.springframework.beans.factory.Aware;
import org.springframework.core.type.AnnotationMetadata;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/ImportAware.class */
public interface ImportAware extends Aware {
    void setImportMetadata(AnnotationMetadata importMetadata);
}
