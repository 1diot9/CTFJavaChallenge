package org.springframework.expression.spel.ast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.ast.ValueRef;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/ast/Selection.class */
public class Selection extends SpelNodeImpl {
    public static final int ALL = 0;
    public static final int FIRST = 1;
    public static final int LAST = 2;
    private final int variant;
    private final boolean nullSafe;

    public Selection(boolean nullSafe, int variant, int startPos, int endPos, SpelNodeImpl expression) {
        super(startPos, endPos, expression);
        this.nullSafe = nullSafe;
        this.variant = variant;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
        return getValueRef(state).getValue();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public ValueRef getValueRef(ExpressionState state) throws EvaluationException {
        Iterable<?> asList;
        TypeDescriptor elementTypeDesc;
        TypedValue op = state.getActiveContextObject();
        Object operand = op.getValue();
        SpelNodeImpl selectionCriteria = this.children[0];
        if (operand instanceof Map) {
            Map<?, ?> mapdata = (Map) operand;
            Map<Object, Object> result = new HashMap<>();
            Object lastKey = null;
            for (Map.Entry<?, ?> entry : mapdata.entrySet()) {
                try {
                    TypedValue kvPair = new TypedValue(entry);
                    state.pushActiveContextObject(kvPair);
                    state.enterScope();
                    Object val = selectionCriteria.getValueInternal(state).getValue();
                    if (val instanceof Boolean) {
                        Boolean b = (Boolean) val;
                        if (b.booleanValue()) {
                            if (this.variant == 1) {
                                result.put(entry.getKey(), entry.getValue());
                                ValueRef.TypedValueHolderValueRef typedValueHolderValueRef = new ValueRef.TypedValueHolderValueRef(new TypedValue(result), this);
                                state.popActiveContextObject();
                                state.exitScope();
                                return typedValueHolderValueRef;
                            }
                            result.put(entry.getKey(), entry.getValue());
                            lastKey = entry.getKey();
                        }
                    } else {
                        throw new SpelEvaluationException(selectionCriteria.getStartPosition(), SpelMessage.RESULT_OF_SELECTION_CRITERIA_IS_NOT_BOOLEAN, new Object[0]);
                    }
                } finally {
                    state.popActiveContextObject();
                    state.exitScope();
                }
            }
            if ((this.variant == 1 || this.variant == 2) && result.isEmpty()) {
                return new ValueRef.TypedValueHolderValueRef(new TypedValue(null), this);
            }
            if (this.variant == 2) {
                Map<Object, Object> resultMap = new HashMap<>();
                Object lastValue = result.get(lastKey);
                resultMap.put(lastKey, lastValue);
                return new ValueRef.TypedValueHolderValueRef(new TypedValue(resultMap), this);
            }
            return new ValueRef.TypedValueHolderValueRef(new TypedValue(result), this);
        }
        if ((operand instanceof Iterable) || ObjectUtils.isArray(operand)) {
            if (operand instanceof Iterable) {
                Iterable<?> iterable = (Iterable) operand;
                asList = iterable;
            } else {
                asList = Arrays.asList(ObjectUtils.toObjectArray(operand));
            }
            Iterable<?> data = asList;
            List<Object> result2 = new ArrayList<>();
            int index = 0;
            for (Object element : data) {
                try {
                    state.pushActiveContextObject(new TypedValue(element));
                    state.enterScope(BeanDefinitionParserDelegate.INDEX_ATTRIBUTE, Integer.valueOf(index));
                    Object val2 = selectionCriteria.getValueInternal(state).getValue();
                    if (val2 instanceof Boolean) {
                        Boolean b2 = (Boolean) val2;
                        if (b2.booleanValue()) {
                            if (this.variant == 1) {
                                ValueRef.TypedValueHolderValueRef typedValueHolderValueRef2 = new ValueRef.TypedValueHolderValueRef(new TypedValue(element), this);
                                state.exitScope();
                                state.popActiveContextObject();
                                return typedValueHolderValueRef2;
                            }
                            result2.add(element);
                        }
                        index++;
                        state.exitScope();
                        state.popActiveContextObject();
                    } else {
                        throw new SpelEvaluationException(selectionCriteria.getStartPosition(), SpelMessage.RESULT_OF_SELECTION_CRITERIA_IS_NOT_BOOLEAN, new Object[0]);
                    }
                } catch (Throwable th) {
                    state.exitScope();
                    state.popActiveContextObject();
                    throw th;
                }
            }
            if ((this.variant == 1 || this.variant == 2) && result2.isEmpty()) {
                return ValueRef.NullValueRef.INSTANCE;
            }
            if (this.variant == 2) {
                return new ValueRef.TypedValueHolderValueRef(new TypedValue(CollectionUtils.lastElement(result2)), this);
            }
            if (operand instanceof Iterable) {
                return new ValueRef.TypedValueHolderValueRef(new TypedValue(result2), this);
            }
            Class<?> elementType = null;
            TypeDescriptor typeDesc = op.getTypeDescriptor();
            if (typeDesc != null && (elementTypeDesc = typeDesc.getElementTypeDescriptor()) != null) {
                elementType = ClassUtils.resolvePrimitiveIfNecessary(elementTypeDesc.getType());
            }
            Assert.state(elementType != null, "Unresolvable element type");
            Object resultArray = Array.newInstance(elementType, result2.size());
            System.arraycopy(result2.toArray(), 0, resultArray, 0, result2.size());
            return new ValueRef.TypedValueHolderValueRef(new TypedValue(resultArray), this);
        }
        if (operand == null) {
            if (this.nullSafe) {
                return ValueRef.NullValueRef.INSTANCE;
            }
            throw new SpelEvaluationException(getStartPosition(), SpelMessage.INVALID_TYPE_FOR_SELECTION, "null");
        }
        throw new SpelEvaluationException(getStartPosition(), SpelMessage.INVALID_TYPE_FOR_SELECTION, operand.getClass().getName());
    }

    @Override // org.springframework.expression.spel.SpelNode
    public String toStringAST() {
        return prefix() + getChild(0).toStringAST() + "]";
    }

    private String prefix() {
        switch (this.variant) {
            case 0:
                return "?[";
            case 1:
                return "^[";
            case 2:
                return "$[";
            default:
                return "";
        }
    }
}
