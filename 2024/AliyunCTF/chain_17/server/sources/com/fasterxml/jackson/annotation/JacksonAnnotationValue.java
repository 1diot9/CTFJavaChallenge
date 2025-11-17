package com.fasterxml.jackson.annotation;

import java.lang.annotation.Annotation;

/* loaded from: server.jar:BOOT-INF/lib/jackson-annotations-2.15.3.jar:com/fasterxml/jackson/annotation/JacksonAnnotationValue.class */
public interface JacksonAnnotationValue<A extends Annotation> {
    Class<A> valueFor();
}
