package com.fasterxml.jackson.databind.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotation;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ser.VirtualBeanPropertyWriter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@JacksonAnnotation
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/annotation/JsonAppend.class */
public @interface JsonAppend {

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/annotation/JsonAppend$Attr.class */
    public @interface Attr {
        String value();

        String propName() default "";

        String propNamespace() default "";

        JsonInclude.Include include() default JsonInclude.Include.NON_NULL;

        boolean required() default false;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/annotation/JsonAppend$Prop.class */
    public @interface Prop {
        Class<? extends VirtualBeanPropertyWriter> value();

        String name() default "";

        String namespace() default "";

        JsonInclude.Include include() default JsonInclude.Include.NON_NULL;

        boolean required() default false;

        Class<?> type() default Object.class;
    }

    Attr[] attrs() default {};

    Prop[] props() default {};

    boolean prepend() default false;
}
