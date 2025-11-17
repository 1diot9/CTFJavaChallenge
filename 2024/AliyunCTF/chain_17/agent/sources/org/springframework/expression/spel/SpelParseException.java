package org.springframework.expression.spel;

import org.springframework.expression.ParseException;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/SpelParseException.class */
public class SpelParseException extends ParseException {
    private final SpelMessage message;
    private final Object[] inserts;

    public SpelParseException(@Nullable String expressionString, int position, SpelMessage message, Object... inserts) {
        super(expressionString, position, message.formatMessage(inserts));
        this.message = message;
        this.inserts = inserts;
    }

    public SpelParseException(int position, SpelMessage message, Object... inserts) {
        super(position, message.formatMessage(inserts));
        this.message = message;
        this.inserts = inserts;
    }

    public SpelParseException(int position, Throwable cause, SpelMessage message, Object... inserts) {
        super(position, message.formatMessage(inserts), cause);
        this.message = message;
        this.inserts = inserts;
    }

    public SpelMessage getMessageCode() {
        return this.message;
    }

    public Object[] getInserts() {
        return this.inserts;
    }
}
