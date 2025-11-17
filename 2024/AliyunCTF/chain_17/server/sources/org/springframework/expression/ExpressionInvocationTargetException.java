package org.springframework.expression;

/* loaded from: server.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/ExpressionInvocationTargetException.class */
public class ExpressionInvocationTargetException extends EvaluationException {
    public ExpressionInvocationTargetException(int position, String message, Throwable cause) {
        super(position, message, cause);
    }

    public ExpressionInvocationTargetException(int position, String message) {
        super(position, message);
    }

    public ExpressionInvocationTargetException(String expressionString, String message) {
        super(expressionString, message);
    }

    public ExpressionInvocationTargetException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExpressionInvocationTargetException(String message) {
        super(message);
    }
}
