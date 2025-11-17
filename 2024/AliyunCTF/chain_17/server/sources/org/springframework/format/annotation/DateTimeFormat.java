package org.springframework.format.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Documented
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/annotation/DateTimeFormat.class */
public @interface DateTimeFormat {

    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/annotation/DateTimeFormat$ISO.class */
    public enum ISO {
        DATE,
        TIME,
        DATE_TIME,
        NONE
    }

    String style() default "SS";

    ISO iso() default ISO.NONE;

    String pattern() default "";

    String[] fallbackPatterns() default {};
}
