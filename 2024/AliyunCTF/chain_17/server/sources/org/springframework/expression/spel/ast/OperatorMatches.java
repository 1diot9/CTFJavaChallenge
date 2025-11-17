package org.springframework.expression.spel.ast;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.support.BooleanTypedValue;

/* loaded from: server.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/ast/OperatorMatches.class */
public class OperatorMatches extends Operator {
    private static final int PATTERN_ACCESS_THRESHOLD = 1000000;
    private static final int MAX_REGEX_LENGTH = 1000;
    private final ConcurrentMap<String, Pattern> patternCache;

    @Deprecated(since = "5.2.23")
    public OperatorMatches(int startPos, int endPos, SpelNodeImpl... operands) {
        this(new ConcurrentHashMap(), startPos, endPos, operands);
    }

    public OperatorMatches(ConcurrentMap<String, Pattern> patternCache, int startPos, int endPos, SpelNodeImpl... operands) {
        super("matches", startPos, endPos, operands);
        this.patternCache = patternCache;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public BooleanTypedValue getValueInternal(ExpressionState state) throws EvaluationException {
        SpelNodeImpl leftOp = getLeftOperand();
        SpelNodeImpl rightOp = getRightOperand();
        String input = (String) leftOp.getValue(state, String.class);
        if (input == null) {
            throw new SpelEvaluationException(leftOp.getStartPosition(), SpelMessage.INVALID_FIRST_OPERAND_FOR_MATCHES_OPERATOR, null);
        }
        Object right = rightOp.getValue(state);
        if (!(right instanceof String)) {
            throw new SpelEvaluationException(rightOp.getStartPosition(), SpelMessage.INVALID_SECOND_OPERAND_FOR_MATCHES_OPERATOR, right);
        }
        String regex = (String) right;
        try {
            Pattern pattern = this.patternCache.get(regex);
            if (pattern == null) {
                checkRegexLength(regex);
                pattern = Pattern.compile(regex);
                this.patternCache.putIfAbsent(regex, pattern);
            }
            Matcher matcher = pattern.matcher(new MatcherInput(input, new AccessCount()));
            return BooleanTypedValue.forValue(matcher.matches());
        } catch (IllegalStateException ex) {
            throw new SpelEvaluationException(rightOp.getStartPosition(), ex, SpelMessage.FLAWED_PATTERN, right);
        } catch (PatternSyntaxException ex2) {
            throw new SpelEvaluationException(rightOp.getStartPosition(), ex2, SpelMessage.INVALID_PATTERN, right);
        }
    }

    private void checkRegexLength(String regex) {
        if (regex.length() > 1000) {
            throw new SpelEvaluationException(getStartPosition(), SpelMessage.MAX_REGEX_LENGTH_EXCEEDED, 1000);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/ast/OperatorMatches$AccessCount.class */
    public static class AccessCount {
        private int count;

        private AccessCount() {
        }

        public void check() throws IllegalStateException {
            int i = this.count;
            this.count = i + 1;
            if (i > OperatorMatches.PATTERN_ACCESS_THRESHOLD) {
                throw new IllegalStateException("Pattern access threshold exceeded");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/ast/OperatorMatches$MatcherInput.class */
    public static class MatcherInput implements CharSequence {
        private final CharSequence value;
        private final AccessCount access;

        public MatcherInput(CharSequence value, AccessCount access) {
            this.value = value;
            this.access = access;
        }

        @Override // java.lang.CharSequence
        public char charAt(int index) {
            this.access.check();
            return this.value.charAt(index);
        }

        @Override // java.lang.CharSequence
        public CharSequence subSequence(int start, int end) {
            return new MatcherInput(this.value.subSequence(start, end), this.access);
        }

        @Override // java.lang.CharSequence
        public int length() {
            return this.value.length();
        }

        @Override // java.lang.CharSequence
        public String toString() {
            return this.value.toString();
        }
    }
}
