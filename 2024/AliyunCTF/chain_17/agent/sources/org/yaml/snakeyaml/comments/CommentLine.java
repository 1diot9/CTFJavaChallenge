package org.yaml.snakeyaml.comments;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.CommentEvent;

/* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/comments/CommentLine.class */
public class CommentLine {
    private final Mark startMark;
    private final Mark endMark;
    private final String value;
    private final CommentType commentType;

    public CommentLine(CommentEvent event) {
        this(event.getStartMark(), event.getEndMark(), event.getValue(), event.getCommentType());
    }

    public CommentLine(Mark startMark, Mark endMark, String value, CommentType commentType) {
        this.startMark = startMark;
        this.endMark = endMark;
        this.value = value;
        this.commentType = commentType;
    }

    public Mark getEndMark() {
        return this.endMark;
    }

    public Mark getStartMark() {
        return this.startMark;
    }

    public CommentType getCommentType() {
        return this.commentType;
    }

    public String getValue() {
        return this.value;
    }

    public String toString() {
        return "<" + getClass().getName() + " (type=" + getCommentType() + ", value=" + getValue() + ")>";
    }
}
