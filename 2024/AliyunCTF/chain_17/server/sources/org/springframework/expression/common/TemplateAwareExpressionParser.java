package org.springframework.expression.common;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.ParserContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/common/TemplateAwareExpressionParser.class */
public abstract class TemplateAwareExpressionParser implements ExpressionParser {
    protected abstract Expression doParseExpression(String expressionString, @Nullable ParserContext context) throws ParseException;

    @Override // org.springframework.expression.ExpressionParser
    public Expression parseExpression(String expressionString) throws ParseException {
        return parseExpression(expressionString, null);
    }

    @Override // org.springframework.expression.ExpressionParser
    public Expression parseExpression(String expressionString, @Nullable ParserContext context) throws ParseException {
        if (context != null && context.isTemplate()) {
            Assert.notNull(expressionString, "'expressionString' must not be null");
            return parseTemplate(expressionString, context);
        }
        Assert.hasText(expressionString, "'expressionString' must not be null or blank");
        return doParseExpression(expressionString, context);
    }

    private Expression parseTemplate(String expressionString, ParserContext context) throws ParseException {
        if (expressionString.isEmpty()) {
            return new LiteralExpression("");
        }
        Expression[] expressions = parseExpressions(expressionString, context);
        if (expressions.length == 1) {
            return expressions[0];
        }
        return new CompositeStringExpression(expressionString, expressions);
    }

    private Expression[] parseExpressions(String expressionString, ParserContext context) throws ParseException {
        List<Expression> expressions = new ArrayList<>();
        String prefix = context.getExpressionPrefix();
        String suffix = context.getExpressionSuffix();
        int i = 0;
        while (true) {
            int startIdx = i;
            if (startIdx < expressionString.length()) {
                int prefixIndex = expressionString.indexOf(prefix, startIdx);
                if (prefixIndex >= startIdx) {
                    if (prefixIndex > startIdx) {
                        expressions.add(new LiteralExpression(expressionString.substring(startIdx, prefixIndex)));
                    }
                    int afterPrefixIndex = prefixIndex + prefix.length();
                    int suffixIndex = skipToCorrectEndSuffix(suffix, expressionString, afterPrefixIndex);
                    if (suffixIndex == -1) {
                        throw new ParseException(expressionString, prefixIndex, "No ending suffix '" + suffix + "' for expression starting at character " + prefixIndex + ": " + expressionString.substring(prefixIndex));
                    }
                    if (suffixIndex == afterPrefixIndex) {
                        throw new ParseException(expressionString, prefixIndex, "No expression defined within delimiter '" + prefix + suffix + "' at character " + prefixIndex);
                    }
                    String expr = expressionString.substring(prefixIndex + prefix.length(), suffixIndex).trim();
                    if (expr.isEmpty()) {
                        throw new ParseException(expressionString, prefixIndex, "No expression defined within delimiter '" + prefix + suffix + "' at character " + prefixIndex);
                    }
                    expressions.add(doParseExpression(expr, context));
                    i = suffixIndex + suffix.length();
                } else {
                    expressions.add(new LiteralExpression(expressionString.substring(startIdx)));
                    i = expressionString.length();
                }
            } else {
                return (Expression[]) expressions.toArray(new Expression[0]);
            }
        }
    }

    private boolean isSuffixHere(String expressionString, int pos, String suffix) {
        int suffixPosition = 0;
        for (int i = 0; i < suffix.length() && pos < expressionString.length(); i++) {
            int i2 = pos;
            pos++;
            int i3 = suffixPosition;
            suffixPosition++;
            if (expressionString.charAt(i2) != suffix.charAt(i3)) {
                return false;
            }
        }
        if (suffixPosition != suffix.length()) {
            return false;
        }
        return true;
    }

    private int skipToCorrectEndSuffix(String suffix, String expressionString, int afterPrefixIndex) throws ParseException {
        int pos = afterPrefixIndex;
        int maxlen = expressionString.length();
        int nextSuffix = expressionString.indexOf(suffix, afterPrefixIndex);
        if (nextSuffix == -1) {
            return -1;
        }
        Deque<Bracket> stack = new ArrayDeque<>();
        while (pos < maxlen && (!isSuffixHere(expressionString, pos, suffix) || !stack.isEmpty())) {
            char ch2 = expressionString.charAt(pos);
            switch (ch2) {
                case '\"':
                case '\'':
                    int endLiteral = expressionString.indexOf(ch2, pos + 1);
                    if (endLiteral == -1) {
                        throw new ParseException(expressionString, pos, "Found non terminating string literal starting at position " + pos);
                    }
                    pos = endLiteral;
                    break;
                case '(':
                case '[':
                case '{':
                    stack.push(new Bracket(ch2, pos));
                    break;
                case ')':
                case ']':
                case '}':
                    if (stack.isEmpty()) {
                        throw new ParseException(expressionString, pos, "Found closing '" + ch2 + "' at position " + pos + " without an opening '" + Bracket.theOpenBracketFor(ch2) + "'");
                    }
                    Bracket p = stack.pop();
                    if (!p.compatibleWithCloseBracket(ch2)) {
                        throw new ParseException(expressionString, pos, "Found closing '" + ch2 + "' at position " + pos + " but most recent opening is '" + p.bracket + "' at position " + p.pos);
                    }
                    break;
            }
            pos++;
        }
        if (!stack.isEmpty()) {
            Bracket p2 = stack.pop();
            throw new ParseException(expressionString, p2.pos, "Missing closing '" + Bracket.theCloseBracketFor(p2.bracket) + "' for '" + p2.bracket + "' at position " + p2.pos);
        }
        if (!isSuffixHere(expressionString, pos, suffix)) {
            return -1;
        }
        return pos;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/common/TemplateAwareExpressionParser$Bracket.class */
    public static class Bracket {
        char bracket;
        int pos;

        Bracket(char bracket, int pos) {
            this.bracket = bracket;
            this.pos = pos;
        }

        boolean compatibleWithCloseBracket(char closeBracket) {
            return this.bracket == '{' ? closeBracket == '}' : this.bracket == '[' ? closeBracket == ']' : closeBracket == ')';
        }

        static char theOpenBracketFor(char closeBracket) {
            if (closeBracket == '}') {
                return '{';
            }
            if (closeBracket == ']') {
                return '[';
            }
            return '(';
        }

        static char theCloseBracketFor(char openBracket) {
            if (openBracket == '{') {
                return '}';
            }
            if (openBracket == '[') {
                return ']';
            }
            return ')';
        }
    }
}
