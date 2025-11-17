package org.springframework.jmx.export.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

@Target({ElementType.TYPE})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Documented
/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/export/annotation/ManagedResource.class */
public @interface ManagedResource {
    @AliasFor("objectName")
    String value() default "";

    @AliasFor("value")
    String objectName() default "";

    String description() default "";

    int currencyTimeLimit() default -1;

    boolean log() default false;

    String logFile() default "";

    String persistPolicy() default "";

    int persistPeriod() default -1;

    String persistName() default "";

    String persistLocation() default "";
}
