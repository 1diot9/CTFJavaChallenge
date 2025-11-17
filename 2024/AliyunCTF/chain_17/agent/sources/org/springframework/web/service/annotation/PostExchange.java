package org.springframework.web.service.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@HttpExchange(method = "POST")
/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/service/annotation/PostExchange.class */
public @interface PostExchange {
    @AliasFor(annotation = HttpExchange.class)
    String value() default "";

    @AliasFor(annotation = HttpExchange.class)
    String url() default "";

    @AliasFor(annotation = HttpExchange.class)
    String contentType() default "";

    @AliasFor(annotation = HttpExchange.class)
    String[] accept() default {};
}
