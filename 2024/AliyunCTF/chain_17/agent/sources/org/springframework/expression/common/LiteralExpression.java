package org.springframework.expression.common;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.TypedValue;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/common/LiteralExpression.class */
public class LiteralExpression implements Expression {
    private final String literalValue;

    public LiteralExpression(String literalValue) {
        this.literalValue = literalValue;
    }

    @Override // org.springframework.expression.Expression
    public final String getExpressionString() {
        return this.literalValue;
    }

    @Override // org.springframework.expression.Expression
    public Class<?> getValueType(EvaluationContext context) {
        return String.class;
    }

    @Override // org.springframework.expression.Expression
    public String getValue() {
        return this.literalValue;
    }

    @Override // org.springframework.expression.Expression
    @Nullable
    public <T> T getValue(@Nullable Class<T> cls) throws EvaluationException {
        return (T) ExpressionUtils.convertTypedValue(null, new TypedValue(getValue()), cls);
    }

    @Override // org.springframework.expression.Expression
    public String getValue(@Nullable Object rootObject) {
        return this.literalValue;
    }

    @Override // org.springframework.expression.Expression
    @Nullable
    public <T> T getValue(@Nullable Object obj, @Nullable Class<T> cls) throws EvaluationException {
        return (T) ExpressionUtils.convertTypedValue(null, new TypedValue(getValue(obj)), cls);
    }

    @Override // org.springframework.expression.Expression
    public String getValue(EvaluationContext context) {
        return this.literalValue;
    }

    @Override // org.springframework.expression.Expression
    @Nullable
    public <T> T getValue(EvaluationContext evaluationContext, @Nullable Class<T> cls) throws EvaluationException {
        return (T) ExpressionUtils.convertTypedValue(evaluationContext, new TypedValue(getValue(evaluationContext)), cls);
    }

    @Override // org.springframework.expression.Expression
    public String getValue(EvaluationContext context, @Nullable Object rootObject) throws EvaluationException {
        return this.literalValue;
    }

    @Override // org.springframework.expression.Expression
    @Nullable
    public <T> T getValue(EvaluationContext evaluationContext, @Nullable Object obj, @Nullable Class<T> cls) throws EvaluationException {
        return (T) ExpressionUtils.convertTypedValue(evaluationContext, new TypedValue(getValue(evaluationContext, obj)), cls);
    }

    @Override // org.springframework.expression.Expression
    public Class<?> getValueType() {
        return String.class;
    }

    @Override // org.springframework.expression.Expression
    public Class<?> getValueType(@Nullable Object rootObject) throws EvaluationException {
        return String.class;
    }

    @Override // org.springframework.expression.Expression
    public Class<?> getValueType(EvaluationContext context, @Nullable Object rootObject) throws EvaluationException {
        return String.class;
    }

    @Override // org.springframework.expression.Expression
    public TypeDescriptor getValueTypeDescriptor() {
        return TypeDescriptor.valueOf(String.class);
    }

    @Override // org.springframework.expression.Expression
    public TypeDescriptor getValueTypeDescriptor(@Nullable Object rootObject) throws EvaluationException {
        return TypeDescriptor.valueOf(String.class);
    }

    @Override // org.springframework.expression.Expression
    public TypeDescriptor getValueTypeDescriptor(EvaluationContext context) {
        return TypeDescriptor.valueOf(String.class);
    }

    @Override // org.springframework.expression.Expression
    public TypeDescriptor getValueTypeDescriptor(EvaluationContext context, @Nullable Object rootObject) throws EvaluationException {
        return TypeDescriptor.valueOf(String.class);
    }

    @Override // org.springframework.expression.Expression
    public boolean isWritable(@Nullable Object rootObject) throws EvaluationException {
        return false;
    }

    @Override // org.springframework.expression.Expression
    public boolean isWritable(EvaluationContext context) {
        return false;
    }

    @Override // org.springframework.expression.Expression
    public boolean isWritable(EvaluationContext context, @Nullable Object rootObject) throws EvaluationException {
        return false;
    }

    @Override // org.springframework.expression.Expression
    public void setValue(@Nullable Object rootObject, @Nullable Object value) throws EvaluationException {
        throw new EvaluationException(this.literalValue, "Cannot call setValue() on a LiteralExpression");
    }

    @Override // org.springframework.expression.Expression
    public void setValue(EvaluationContext context, @Nullable Object value) throws EvaluationException {
        throw new EvaluationException(this.literalValue, "Cannot call setValue() on a LiteralExpression");
    }

    @Override // org.springframework.expression.Expression
    public void setValue(EvaluationContext context, @Nullable Object rootObject, @Nullable Object value) throws EvaluationException {
        throw new EvaluationException(this.literalValue, "Cannot call setValue() on a LiteralExpression");
    }
}
