package ch.qos.logback.core.joran.event;

import org.xml.sax.Locator;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/event/EndEvent.class */
public class EndEvent extends SaxEvent {
    /* JADX INFO: Access modifiers changed from: package-private */
    public EndEvent(String namespaceURI, String localName, String qName, Locator locator) {
        super(namespaceURI, localName, qName, locator);
    }

    public String toString() {
        return "  EndEvent(" + getQName() + ")  [" + this.locator.getLineNumber() + "," + this.locator.getColumnNumber() + "]";
    }
}
