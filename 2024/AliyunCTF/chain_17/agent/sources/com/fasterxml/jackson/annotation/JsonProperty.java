package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@JacksonAnnotation
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: agent.jar:BOOT-INF/lib/jackson-annotations-2.15.3.jar:com/fasterxml/jackson/annotation/JsonProperty.class */
public @interface JsonProperty {
    public static final String USE_DEFAULT_NAME = "";
    public static final int INDEX_UNKNOWN = -1;

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-annotations-2.15.3.jar:com/fasterxml/jackson/annotation/JsonProperty$Access.class */
    public enum Access {
        AUTO,
        READ_ONLY,
        WRITE_ONLY,
        READ_WRITE
    }

    String value() default "";

    String namespace() default "";

    boolean required() default false;

    int index() default -1;

    String defaultValue() default "";

    Access access() default Access.AUTO;
}
