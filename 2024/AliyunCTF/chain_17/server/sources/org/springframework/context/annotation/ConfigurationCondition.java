package org.springframework.context.annotation;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/ConfigurationCondition.class */
public interface ConfigurationCondition extends Condition {

    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/ConfigurationCondition$ConfigurationPhase.class */
    public enum ConfigurationPhase {
        PARSE_CONFIGURATION,
        REGISTER_BEAN
    }

    ConfigurationPhase getConfigurationPhase();
}
