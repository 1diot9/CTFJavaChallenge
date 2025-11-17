package org.springframework.scheduling.config;

import org.springframework.scheduling.Trigger;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/config/TriggerTask.class */
public class TriggerTask extends Task {
    private final Trigger trigger;

    public TriggerTask(Runnable runnable, Trigger trigger) {
        super(runnable);
        Assert.notNull(trigger, "Trigger must not be null");
        this.trigger = trigger;
    }

    public Trigger getTrigger() {
        return this.trigger;
    }
}
