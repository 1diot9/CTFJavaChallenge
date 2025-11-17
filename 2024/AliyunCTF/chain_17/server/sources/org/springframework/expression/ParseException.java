package org.springframework.expression;

import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/ParseException.class */
public class ParseException extends ExpressionException {
    public ParseException(@Nullable String expressionString, int position, String message) {
        super(expressionString, position, message);
    }

    public ParseException(int position, String message, Throwable cause) {
        super(position, message, cause);
    }

    public ParseException(int position, String message) {
        super(position, message);
    }
}
