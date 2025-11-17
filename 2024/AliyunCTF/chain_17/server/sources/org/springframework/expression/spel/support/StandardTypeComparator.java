package org.springframework.expression.spel.support;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.springframework.expression.TypeComparator;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.NumberUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/support/StandardTypeComparator.class */
public class StandardTypeComparator implements TypeComparator {
    static final StandardTypeComparator INSTANCE = new StandardTypeComparator();

    @Override // org.springframework.expression.TypeComparator
    public boolean canCompare(@Nullable Object left, @Nullable Object right) {
        Class<?> ancestor;
        if (left == null || right == null) {
            return true;
        }
        if ((left instanceof Number) && (right instanceof Number)) {
            return true;
        }
        return (left instanceof Comparable) && (right instanceof Comparable) && (ancestor = ClassUtils.determineCommonAncestor(left.getClass(), right.getClass())) != null && Comparable.class.isAssignableFrom(ancestor);
    }

    @Override // org.springframework.expression.TypeComparator
    public int compare(@Nullable Object left, @Nullable Object right) throws SpelEvaluationException {
        if (left == null) {
            return right == null ? 0 : -1;
        }
        if (right == null) {
            return 1;
        }
        if (left instanceof Number) {
            Number leftNumber = (Number) left;
            if (right instanceof Number) {
                Number rightNumber = (Number) right;
                if ((leftNumber instanceof BigDecimal) || (rightNumber instanceof BigDecimal)) {
                    BigDecimal leftBigDecimal = (BigDecimal) NumberUtils.convertNumberToTargetClass(leftNumber, BigDecimal.class);
                    BigDecimal rightBigDecimal = (BigDecimal) NumberUtils.convertNumberToTargetClass(rightNumber, BigDecimal.class);
                    return leftBigDecimal.compareTo(rightBigDecimal);
                }
                if ((leftNumber instanceof Double) || (rightNumber instanceof Double)) {
                    return Double.compare(leftNumber.doubleValue(), rightNumber.doubleValue());
                }
                if ((leftNumber instanceof Float) || (rightNumber instanceof Float)) {
                    return Float.compare(leftNumber.floatValue(), rightNumber.floatValue());
                }
                if ((leftNumber instanceof BigInteger) || (rightNumber instanceof BigInteger)) {
                    BigInteger leftBigInteger = (BigInteger) NumberUtils.convertNumberToTargetClass(leftNumber, BigInteger.class);
                    BigInteger rightBigInteger = (BigInteger) NumberUtils.convertNumberToTargetClass(rightNumber, BigInteger.class);
                    return leftBigInteger.compareTo(rightBigInteger);
                }
                if ((leftNumber instanceof Long) || (rightNumber instanceof Long)) {
                    return Long.compare(leftNumber.longValue(), rightNumber.longValue());
                }
                if ((leftNumber instanceof Integer) || (rightNumber instanceof Integer)) {
                    return Integer.compare(leftNumber.intValue(), rightNumber.intValue());
                }
                if ((leftNumber instanceof Short) || (rightNumber instanceof Short)) {
                    return Short.compare(leftNumber.shortValue(), rightNumber.shortValue());
                }
                if ((leftNumber instanceof Byte) || (rightNumber instanceof Byte)) {
                    return Byte.compare(leftNumber.byteValue(), rightNumber.byteValue());
                }
                return Double.compare(leftNumber.doubleValue(), rightNumber.doubleValue());
            }
        }
        try {
            if (left instanceof Comparable) {
                Comparable comparable = (Comparable) left;
                return comparable.compareTo(right);
            }
            throw new SpelEvaluationException(SpelMessage.NOT_COMPARABLE, left.getClass(), right.getClass());
        } catch (ClassCastException ex) {
            throw new SpelEvaluationException(ex, SpelMessage.NOT_COMPARABLE, left.getClass(), right.getClass());
        }
    }
}
