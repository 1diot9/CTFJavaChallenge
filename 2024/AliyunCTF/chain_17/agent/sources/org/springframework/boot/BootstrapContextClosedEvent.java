package org.springframework.boot;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/BootstrapContextClosedEvent.class */
public class BootstrapContextClosedEvent extends ApplicationEvent {
    private final ConfigurableApplicationContext applicationContext;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BootstrapContextClosedEvent(BootstrapContext source, ConfigurableApplicationContext applicationContext) {
        super(source);
        this.applicationContext = applicationContext;
    }

    public BootstrapContext getBootstrapContext() {
        return (BootstrapContext) this.source;
    }

    public ConfigurableApplicationContext getApplicationContext() {
        return this.applicationContext;
    }
}
