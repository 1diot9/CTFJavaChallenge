package org.springframework.web.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.aot.hint.annotation.Reflective;

@Target({ElementType.METHOD})
@Reflective({ExceptionHandlerReflectiveProcessor.class})
@Retention(RetentionPolicy.RUNTIME)
@Documented
/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/bind/annotation/ExceptionHandler.class */
public @interface ExceptionHandler {
    Class<? extends Throwable>[] value() default {};
}
