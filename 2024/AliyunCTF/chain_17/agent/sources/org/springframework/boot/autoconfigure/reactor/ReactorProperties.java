package org.springframework.boot.autoconfigure.reactor;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.reactor")
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/reactor/ReactorProperties.class */
public class ReactorProperties {
    private ContextPropagationMode contextPropagation = ContextPropagationMode.LIMITED;

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/reactor/ReactorProperties$ContextPropagationMode.class */
    public enum ContextPropagationMode {
        AUTO,
        LIMITED
    }

    public ContextPropagationMode getContextPropagation() {
        return this.contextPropagation;
    }

    public void setContextPropagation(ContextPropagationMode contextPropagation) {
        this.contextPropagation = contextPropagation;
    }
}
