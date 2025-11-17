package org.springframework.boot.jackson;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jackson/JsonComponent.class */
public @interface JsonComponent {

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jackson/JsonComponent$Scope.class */
    public enum Scope {
        VALUES,
        KEYS
    }

    @AliasFor(annotation = Component.class)
    String value() default "";

    Class<?>[] type() default {};

    Scope scope() default Scope.VALUES;
}
