package org.springframework.web.service.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.aot.hint.annotation.Reflective;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.Mapping;

@Target({ElementType.TYPE, ElementType.METHOD})
@Reflective({HttpExchangeReflectiveProcessor.class})
@Mapping
@Retention(RetentionPolicy.RUNTIME)
@Documented
/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/service/annotation/HttpExchange.class */
public @interface HttpExchange {
    @AliasFor("url")
    String value() default "";

    @AliasFor("value")
    String url() default "";

    String method() default "";

    String contentType() default "";

    String[] accept() default {};
}
