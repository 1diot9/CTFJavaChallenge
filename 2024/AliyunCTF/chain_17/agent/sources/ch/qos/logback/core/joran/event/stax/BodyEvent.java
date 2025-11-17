package ch.qos.logback.core.joran.event.stax;

import javax.xml.stream.Location;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/event/stax/BodyEvent.class */
public class BodyEvent extends StaxEvent {
    private String text;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BodyEvent(String text, Location location) {
        super(null, location);
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void append(String txt) {
        this.text += txt;
    }

    public String toString() {
        return "BodyEvent(" + getText() + ")" + this.location.getLineNumber() + "," + this.location.getColumnNumber();
    }
}
