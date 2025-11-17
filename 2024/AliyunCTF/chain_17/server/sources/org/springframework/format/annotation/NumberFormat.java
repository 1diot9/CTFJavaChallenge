package org.springframework.format.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Documented
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/annotation/NumberFormat.class */
public @interface NumberFormat {

    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/annotation/NumberFormat$Style.class */
    public enum Style {
        DEFAULT,
        NUMBER,
        PERCENT,
        CURRENCY
    }

    Style style() default Style.DEFAULT;

    String pattern() default "";
}
