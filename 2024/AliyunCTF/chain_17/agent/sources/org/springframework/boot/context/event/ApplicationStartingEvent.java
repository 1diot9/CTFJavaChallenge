package org.springframework.boot.context.event;

import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/event/ApplicationStartingEvent.class */
public class ApplicationStartingEvent extends SpringApplicationEvent {
    private final ConfigurableBootstrapContext bootstrapContext;

    public ApplicationStartingEvent(ConfigurableBootstrapContext bootstrapContext, SpringApplication application, String[] args) {
        super(application, args);
        this.bootstrapContext = bootstrapContext;
    }

    public ConfigurableBootstrapContext getBootstrapContext() {
        return this.bootstrapContext;
    }
}
