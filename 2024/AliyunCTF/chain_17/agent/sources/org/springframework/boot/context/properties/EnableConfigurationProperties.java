package org.springframework.boot.context.properties;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({EnableConfigurationPropertiesRegistrar.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/EnableConfigurationProperties.class */
public @interface EnableConfigurationProperties {
    public static final String VALIDATOR_BEAN_NAME = "configurationPropertiesValidator";

    Class<?>[] value() default {};
}
