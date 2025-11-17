package ch.qos.logback.core.joran.event.stax;

import javax.xml.stream.Location;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/event/stax/EndEvent.class */
public class EndEvent extends StaxEvent {
    public EndEvent(String name, Location location) {
        super(name, location);
    }

    public String toString() {
        return "EndEvent(" + getName() + ")  [" + this.location.getLineNumber() + "," + this.location.getColumnNumber() + "]";
    }
}
