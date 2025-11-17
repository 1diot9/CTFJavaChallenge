package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@JacksonAnnotation
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: server.jar:BOOT-INF/lib/jackson-annotations-2.15.3.jar:com/fasterxml/jackson/annotation/JsonTypeInfo.class */
public @interface JsonTypeInfo {

    /* loaded from: server.jar:BOOT-INF/lib/jackson-annotations-2.15.3.jar:com/fasterxml/jackson/annotation/JsonTypeInfo$As.class */
    public enum As {
        PROPERTY,
        WRAPPER_OBJECT,
        WRAPPER_ARRAY,
        EXTERNAL_PROPERTY,
        EXISTING_PROPERTY
    }

    @Deprecated
    /* loaded from: server.jar:BOOT-INF/lib/jackson-annotations-2.15.3.jar:com/fasterxml/jackson/annotation/JsonTypeInfo$None.class */
    public static abstract class None {
    }

    Id use();

    As include() default As.PROPERTY;

    String property() default "";

    Class<?> defaultImpl() default JsonTypeInfo.class;

    boolean visible() default false;

    /* loaded from: server.jar:BOOT-INF/lib/jackson-annotations-2.15.3.jar:com/fasterxml/jackson/annotation/JsonTypeInfo$Id.class */
    public enum Id {
        NONE(null),
        CLASS("@class"),
        MINIMAL_CLASS("@c"),
        NAME("@type"),
        DEDUCTION(null),
        CUSTOM(null);

        private final String _defaultPropertyName;

        Id(String defProp) {
            this._defaultPropertyName = defProp;
        }

        public String getDefaultPropertyName() {
            return this._defaultPropertyName;
        }
    }
}
