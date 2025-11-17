package org.springframework.boot.context.event;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/event/ApplicationFailedEvent.class */
public class ApplicationFailedEvent extends SpringApplicationEvent {
    private final ConfigurableApplicationContext context;
    private final Throwable exception;

    public ApplicationFailedEvent(SpringApplication application, String[] args, ConfigurableApplicationContext context, Throwable exception) {
        super(application, args);
        this.context = context;
        this.exception = exception;
    }

    public ConfigurableApplicationContext getApplicationContext() {
        return this.context;
    }

    public Throwable getException() {
        return this.exception;
    }
}
