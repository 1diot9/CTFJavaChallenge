package org.springframework.boot.autoconfigure;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AliasFor;

@Target({ElementType.TYPE})
@AutoConfigureBefore
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter
@Retention(RetentionPolicy.RUNTIME)
@Documented
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/AutoConfiguration.class */
public @interface AutoConfiguration {
    @AliasFor(annotation = Configuration.class)
    String value() default "";

    @AliasFor(annotation = AutoConfigureBefore.class, attribute = "value")
    Class<?>[] before() default {};

    @AliasFor(annotation = AutoConfigureBefore.class, attribute = "name")
    String[] beforeName() default {};

    @AliasFor(annotation = AutoConfigureAfter.class, attribute = "value")
    Class<?>[] after() default {};

    @AliasFor(annotation = AutoConfigureAfter.class, attribute = "name")
    String[] afterName() default {};
}
