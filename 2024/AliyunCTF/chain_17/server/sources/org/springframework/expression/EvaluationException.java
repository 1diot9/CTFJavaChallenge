package org.springframework.expression;

/* loaded from: server.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/EvaluationException.class */
public class EvaluationException extends ExpressionException {
    public EvaluationException(String message) {
        super(message);
    }

    public EvaluationException(String message, Throwable cause) {
        super(message, cause);
    }

    public EvaluationException(int position, String message) {
        super(position, message);
    }

    public EvaluationException(String expressionString, String message) {
        super(expressionString, message);
    }

    public EvaluationException(int position, String message, Throwable cause) {
        super(position, message, cause);
    }
}
