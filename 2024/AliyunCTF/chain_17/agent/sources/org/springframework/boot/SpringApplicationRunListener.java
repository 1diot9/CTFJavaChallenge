package org.springframework.boot;

import java.time.Duration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/SpringApplicationRunListener.class */
public interface SpringApplicationRunListener {
    default void starting(ConfigurableBootstrapContext bootstrapContext) {
    }

    default void environmentPrepared(ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment environment) {
    }

    default void contextPrepared(ConfigurableApplicationContext context) {
    }

    default void contextLoaded(ConfigurableApplicationContext context) {
    }

    default void started(ConfigurableApplicationContext context, Duration timeTaken) {
    }

    default void ready(ConfigurableApplicationContext context, Duration timeTaken) {
    }

    default void failed(ConfigurableApplicationContext context, Throwable exception) {
    }
}
