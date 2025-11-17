package org.springframework.boot.context.properties;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.beans.factory.annotation.Qualifier;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Qualifier(ConfigurationPropertiesBinding.VALUE)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/ConfigurationPropertiesBinding.class */
public @interface ConfigurationPropertiesBinding {
    public static final String VALUE = "org.springframework.boot.context.properties.ConfigurationPropertiesBinding";
}
