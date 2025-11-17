package org.springframework.scheduling.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;
import org.springframework.aot.hint.annotation.Reflective;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Reflective
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(Schedules.class)
/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/annotation/Scheduled.class */
public @interface Scheduled {
    public static final String CRON_DISABLED = "-";

    String cron() default "";

    String zone() default "";

    long fixedDelay() default -1;

    String fixedDelayString() default "";

    long fixedRate() default -1;

    String fixedRateString() default "";

    long initialDelay() default -1;

    String initialDelayString() default "";

    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    String scheduler() default "";
}
