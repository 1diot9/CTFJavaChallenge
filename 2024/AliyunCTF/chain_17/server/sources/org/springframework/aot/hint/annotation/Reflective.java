package org.springframework.aot.hint.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/annotation/Reflective.class */
public @interface Reflective {
    @AliasFor("processors")
    Class<? extends ReflectiveProcessor>[] value() default {SimpleReflectiveProcessor.class};

    @AliasFor("value")
    Class<? extends ReflectiveProcessor>[] processors() default {SimpleReflectiveProcessor.class};
}
