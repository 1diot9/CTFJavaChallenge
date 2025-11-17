package io.micrometer.observation;

import io.micrometer.observation.Observation;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/micrometer-observation-1.12.2.jar:io/micrometer/observation/SimpleEvent.class */
public class SimpleEvent implements Observation.Event {
    private final String name;
    private final String contextualName;
    private final long wallTime;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SimpleEvent(String name, String contextualName) {
        this(name, contextualName, System.currentTimeMillis());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SimpleEvent(String name, String contextualName, long wallTime) {
        this.name = name;
        this.contextualName = contextualName;
        this.wallTime = wallTime;
    }

    @Override // io.micrometer.observation.Observation.Event
    public String getName() {
        return this.name;
    }

    @Override // io.micrometer.observation.Observation.Event
    public String getContextualName() {
        return this.contextualName;
    }

    @Override // io.micrometer.observation.Observation.Event
    public long getWallTime() {
        return this.wallTime;
    }

    public String toString() {
        return "event.name='" + getName() + "', event.contextualName='" + getContextualName() + "', event.wallTime=" + getWallTime();
    }
}
