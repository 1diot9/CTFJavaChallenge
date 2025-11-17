package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.comments.CommentType;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.Event;

/* loaded from: server.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/events/CommentEvent.class */
public final class CommentEvent extends Event {
    private final CommentType type;
    private final String value;

    public CommentEvent(CommentType type, String value, Mark startMark, Mark endMark) {
        super(startMark, endMark);
        if (type == null) {
            throw new NullPointerException("Event Type must be provided.");
        }
        this.type = type;
        if (value == null) {
            throw new NullPointerException("Value must be provided.");
        }
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public CommentType getCommentType() {
        return this.type;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.yaml.snakeyaml.events.Event
    public String getArguments() {
        return super.getArguments() + "type=" + this.type + ", value=" + this.value;
    }

    @Override // org.yaml.snakeyaml.events.Event
    public Event.ID getEventId() {
        return Event.ID.Comment;
    }
}
