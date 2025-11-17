package org.springframework.boot.context.event;

import java.time.Duration;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/event/ApplicationStartedEvent.class */
public class ApplicationStartedEvent extends SpringApplicationEvent {
    private final ConfigurableApplicationContext context;
    private final Duration timeTaken;

    public ApplicationStartedEvent(SpringApplication application, String[] args, ConfigurableApplicationContext context, Duration timeTaken) {
        super(application, args);
        this.context = context;
        this.timeTaken = timeTaken;
    }

    public ConfigurableApplicationContext getApplicationContext() {
        return this.context;
    }

    public Duration getTimeTaken() {
        return this.timeTaken;
    }
}
