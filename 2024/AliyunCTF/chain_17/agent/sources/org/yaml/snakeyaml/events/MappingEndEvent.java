package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.Event;

/* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/events/MappingEndEvent.class */
public final class MappingEndEvent extends CollectionEndEvent {
    public MappingEndEvent(Mark startMark, Mark endMark) {
        super(startMark, endMark);
    }

    @Override // org.yaml.snakeyaml.events.Event
    public Event.ID getEventId() {
        return Event.ID.MappingEnd;
    }
}
