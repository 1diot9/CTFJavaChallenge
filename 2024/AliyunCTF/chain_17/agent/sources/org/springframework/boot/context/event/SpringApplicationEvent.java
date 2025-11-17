package org.springframework.boot.context.event;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationEvent;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/event/SpringApplicationEvent.class */
public abstract class SpringApplicationEvent extends ApplicationEvent {
    private final String[] args;

    public SpringApplicationEvent(SpringApplication application, String[] args) {
        super(application);
        this.args = args;
    }

    public SpringApplication getSpringApplication() {
        return (SpringApplication) getSource();
    }

    public final String[] getArgs() {
        return this.args;
    }
}
