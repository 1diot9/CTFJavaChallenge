package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.Event;

/* loaded from: server.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/events/DocumentEndEvent.class */
public final class DocumentEndEvent extends Event {
    private final boolean explicit;

    public DocumentEndEvent(Mark startMark, Mark endMark, boolean explicit) {
        super(startMark, endMark);
        this.explicit = explicit;
    }

    public boolean getExplicit() {
        return this.explicit;
    }

    @Override // org.yaml.snakeyaml.events.Event
    public Event.ID getEventId() {
        return Event.ID.DocumentEnd;
    }
}
