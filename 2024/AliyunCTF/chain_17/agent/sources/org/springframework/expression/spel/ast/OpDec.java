package org.springframework.expression.spel.ast;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Operation;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/ast/OpDec.class */
public class OpDec extends Operator {
    private final boolean postfix;

    public OpDec(int startPos, int endPos, boolean postfix, SpelNodeImpl... operands) {
        super("--", startPos, endPos, operands);
        this.postfix = postfix;
        Assert.notEmpty(operands, "Operands must not be empty");
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
        SpelNodeImpl operand = getLeftOperand();
        ValueRef lvalue = operand.getValueRef(state);
        TypedValue operandTypedValue = lvalue.getValue();
        Object operandValue = operandTypedValue.getValue();
        TypedValue returnValue = operandTypedValue;
        TypedValue newValue = null;
        if (operandValue instanceof Number) {
            Number op1 = (Number) operandValue;
            if (op1 instanceof BigDecimal) {
                BigDecimal bigDecimal = (BigDecimal) op1;
                newValue = new TypedValue(bigDecimal.subtract(BigDecimal.ONE), operandTypedValue.getTypeDescriptor());
            } else if (op1 instanceof Double) {
                newValue = new TypedValue(Double.valueOf(op1.doubleValue() - 1.0d), operandTypedValue.getTypeDescriptor());
            } else if (op1 instanceof Float) {
                newValue = new TypedValue(Float.valueOf(op1.floatValue() - 1.0f), operandTypedValue.getTypeDescriptor());
            } else if (op1 instanceof BigInteger) {
                BigInteger bigInteger = (BigInteger) op1;
                newValue = new TypedValue(bigInteger.subtract(BigInteger.ONE), operandTypedValue.getTypeDescriptor());
            } else if (op1 instanceof Long) {
                newValue = new TypedValue(Long.valueOf(op1.longValue() - 1), operandTypedValue.getTypeDescriptor());
            } else if (op1 instanceof Integer) {
                newValue = new TypedValue(Integer.valueOf(op1.intValue() - 1), operandTypedValue.getTypeDescriptor());
            } else if (op1 instanceof Short) {
                newValue = new TypedValue(Integer.valueOf(op1.shortValue() - 1), operandTypedValue.getTypeDescriptor());
            } else if (op1 instanceof Byte) {
                newValue = new TypedValue(Integer.valueOf(op1.byteValue() - 1), operandTypedValue.getTypeDescriptor());
            } else {
                newValue = new TypedValue(Double.valueOf(op1.doubleValue() - 1.0d), operandTypedValue.getTypeDescriptor());
            }
        }
        if (newValue == null) {
            try {
                newValue = state.operate(Operation.SUBTRACT, returnValue.getValue(), 1);
            } catch (SpelEvaluationException ex) {
                if (ex.getMessageCode() == SpelMessage.OPERATOR_NOT_SUPPORTED_BETWEEN_TYPES) {
                    throw new SpelEvaluationException(operand.getStartPosition(), SpelMessage.OPERAND_NOT_DECREMENTABLE, operand.toStringAST());
                }
                throw ex;
            }
        }
        try {
            lvalue.setValue(newValue.getValue());
            if (!this.postfix) {
                returnValue = newValue;
            }
            return returnValue;
        } catch (SpelEvaluationException see) {
            if (see.getMessageCode() == SpelMessage.SETVALUE_NOT_SUPPORTED) {
                throw new SpelEvaluationException(operand.getStartPosition(), SpelMessage.OPERAND_NOT_DECREMENTABLE, new Object[0]);
            }
            throw see;
        }
    }

    @Override // org.springframework.expression.spel.ast.Operator, org.springframework.expression.spel.SpelNode
    public String toStringAST() {
        return getLeftOperand().toStringAST() + "--";
    }

    @Override // org.springframework.expression.spel.ast.Operator
    public SpelNodeImpl getRightOperand() {
        throw new IllegalStateException("No right operand");
    }
}
