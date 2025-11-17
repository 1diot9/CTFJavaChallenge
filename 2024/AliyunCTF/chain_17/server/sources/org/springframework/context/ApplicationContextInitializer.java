package org.springframework.context;

import org.springframework.context.ConfigurableApplicationContext;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/ApplicationContextInitializer.class */
public interface ApplicationContextInitializer<C extends ConfigurableApplicationContext> {
    void initialize(C applicationContext);
}
