package org.springframework.expression.spel.ast;

import cn.hutool.core.text.StrPool;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/ast/InlineMap.class */
public class InlineMap extends SpelNodeImpl {

    @Nullable
    private final TypedValue constant;

    public InlineMap(int startPos, int endPos, SpelNodeImpl... args) {
        super(startPos, endPos, args);
        this.constant = computeConstantValue();
    }

    @Nullable
    private TypedValue computeConstantValue() {
        Object key;
        int max = getChildCount();
        for (int c = 0; c < max; c++) {
            SpelNode child = getChild(c);
            if (!(child instanceof Literal)) {
                if (child instanceof InlineList) {
                    InlineList inlineList = (InlineList) child;
                    if (!inlineList.isConstant()) {
                        return null;
                    }
                } else if (child instanceof InlineMap) {
                    InlineMap inlineMap = (InlineMap) child;
                    if (!inlineMap.isConstant()) {
                        return null;
                    }
                } else if (c % 2 != 0 || !(child instanceof PropertyOrFieldReference)) {
                    if (!(child instanceof OpMinus)) {
                        return null;
                    }
                    OpMinus opMinus = (OpMinus) child;
                    if (!opMinus.isNegativeNumberLiteral()) {
                        return null;
                    }
                }
            }
        }
        Map<Object, Object> constantMap = new LinkedHashMap<>();
        int childCount = getChildCount();
        ExpressionState expressionState = new ExpressionState(new StandardEvaluationContext());
        int c2 = 0;
        while (c2 < childCount) {
            int i = c2;
            int c3 = c2 + 1;
            SpelNode keyChild = getChild(i);
            if (keyChild instanceof Literal) {
                Literal literal = (Literal) keyChild;
                key = literal.getLiteralValue().getValue();
            } else if (keyChild instanceof PropertyOrFieldReference) {
                PropertyOrFieldReference propertyOrFieldReference = (PropertyOrFieldReference) keyChild;
                key = propertyOrFieldReference.getName();
            } else if (keyChild instanceof OpMinus) {
                key = keyChild.getValue(expressionState);
            } else {
                return null;
            }
            SpelNode valueChild = getChild(c3);
            Object value = null;
            if (valueChild instanceof Literal) {
                Literal literal2 = (Literal) valueChild;
                value = literal2.getLiteralValue().getValue();
            } else if (valueChild instanceof InlineList) {
                InlineList inlineList2 = (InlineList) valueChild;
                value = inlineList2.getConstantValue();
            } else if (valueChild instanceof InlineMap) {
                InlineMap inlineMap2 = (InlineMap) valueChild;
                value = inlineMap2.getConstantValue();
            } else if (valueChild instanceof OpMinus) {
                value = valueChild.getValue(expressionState);
            }
            constantMap.put(key, value);
            c2 = c3 + 1;
        }
        return new TypedValue(Collections.unmodifiableMap(constantMap));
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public TypedValue getValueInternal(ExpressionState expressionState) throws EvaluationException {
        Object value;
        if (this.constant != null) {
            return this.constant;
        }
        Map<Object, Object> returnValue = new LinkedHashMap<>();
        int childcount = getChildCount();
        int c = 0;
        while (c < childcount) {
            int i = c;
            int c2 = c + 1;
            SpelNode keyChild = getChild(i);
            if (keyChild instanceof PropertyOrFieldReference) {
                PropertyOrFieldReference reference = (PropertyOrFieldReference) keyChild;
                value = reference.getName();
            } else {
                value = keyChild.getValue(expressionState);
            }
            Object key = value;
            Object value2 = getChild(c2).getValue(expressionState);
            returnValue.put(key, value2);
            c = c2 + 1;
        }
        return new TypedValue(returnValue);
    }

    @Override // org.springframework.expression.spel.SpelNode
    public String toStringAST() {
        StringBuilder sb = new StringBuilder(StrPool.DELIM_START);
        int count = getChildCount();
        int c = 0;
        while (c < count) {
            if (c > 0) {
                sb.append(',');
            }
            int i = c;
            int c2 = c + 1;
            sb.append(getChild(i).toStringAST());
            sb.append(':');
            sb.append(getChild(c2).toStringAST());
            c = c2 + 1;
        }
        sb.append('}');
        return sb.toString();
    }

    public boolean isConstant() {
        return this.constant != null;
    }

    @Nullable
    public Map<Object, Object> getConstantValue() {
        Assert.state(this.constant != null, "No constant");
        return (Map) this.constant.getValue();
    }
}
