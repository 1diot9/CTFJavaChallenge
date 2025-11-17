package org.springframework.format;

import java.lang.annotation.Annotation;
import java.util.Set;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/AnnotationFormatterFactory.class */
public interface AnnotationFormatterFactory<A extends Annotation> {
    Set<Class<?>> getFieldTypes();

    Printer<?> getPrinter(A annotation, Class<?> fieldType);

    Parser<?> getParser(A annotation, Class<?> fieldType);
}
