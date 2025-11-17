package org.springframework.expression;

/* loaded from: server.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/ExpressionParser.class */
public interface ExpressionParser {
    Expression parseExpression(String expressionString) throws ParseException;

    Expression parseExpression(String expressionString, ParserContext context) throws ParseException;
}
