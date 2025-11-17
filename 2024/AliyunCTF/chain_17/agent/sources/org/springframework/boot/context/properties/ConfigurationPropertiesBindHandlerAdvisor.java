package org.springframework.boot.context.properties;

import org.springframework.boot.context.properties.bind.BindHandler;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/ConfigurationPropertiesBindHandlerAdvisor.class */
public interface ConfigurationPropertiesBindHandlerAdvisor {
    BindHandler apply(BindHandler bindHandler);
}
