package org.springframework.web.util.pattern;

import java.text.MessageFormat;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/pattern/PatternParseException.class */
public class PatternParseException extends IllegalArgumentException {
    private final int position;
    private final char[] pattern;
    private final PatternMessage messageType;
    private final Object[] inserts;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PatternParseException(int pos, char[] pattern, PatternMessage messageType, Object... inserts) {
        super(messageType.formatMessage(inserts));
        this.position = pos;
        this.pattern = pattern;
        this.messageType = messageType;
        this.inserts = inserts;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PatternParseException(Throwable cause, int pos, char[] pattern, PatternMessage messageType, Object... inserts) {
        super(messageType.formatMessage(inserts), cause);
        this.position = pos;
        this.pattern = pattern;
        this.messageType = messageType;
        this.inserts = inserts;
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        return this.messageType.formatMessage(this.inserts);
    }

    public String toDetailedString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.pattern).append('\n');
        sb.append(" ".repeat(Math.max(0, this.position)));
        sb.append("^\n");
        sb.append(getMessage());
        return sb.toString();
    }

    public int getPosition() {
        return this.position;
    }

    public PatternMessage getMessageType() {
        return this.messageType;
    }

    public Object[] getInserts() {
        return this.inserts;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/pattern/PatternParseException$PatternMessage.class */
    public enum PatternMessage {
        MISSING_CLOSE_CAPTURE("Expected close capture character after variable name '}'"),
        MISSING_OPEN_CAPTURE("Missing preceding open capture character before variable name'{'"),
        ILLEGAL_NESTED_CAPTURE("Not allowed to nest variable captures"),
        CANNOT_HAVE_ADJACENT_CAPTURES("Adjacent captures are not allowed"),
        ILLEGAL_CHARACTER_AT_START_OF_CAPTURE_DESCRIPTOR("Char ''{0}'' not allowed at start of captured variable name"),
        ILLEGAL_CHARACTER_IN_CAPTURE_DESCRIPTOR("Char ''{0}'' is not allowed in a captured variable name"),
        NO_MORE_DATA_EXPECTED_AFTER_CAPTURE_THE_REST("No more pattern data allowed after '{*...}' or '**' pattern element"),
        BADLY_FORMED_CAPTURE_THE_REST("Expected form when capturing the rest of the path is simply '{*...}'"),
        MISSING_REGEX_CONSTRAINT("Missing regex constraint on capture"),
        ILLEGAL_DOUBLE_CAPTURE("Not allowed to capture ''{0}'' twice in the same pattern"),
        REGEX_PATTERN_SYNTAX_EXCEPTION("Exception occurred in regex pattern compilation"),
        CAPTURE_ALL_IS_STANDALONE_CONSTRUCT("'{*...}' can only be preceded by a path separator");

        private final String message;

        PatternMessage(String message) {
            this.message = message;
        }

        public String formatMessage(Object... inserts) {
            return MessageFormat.format(this.message, inserts);
        }
    }
}
