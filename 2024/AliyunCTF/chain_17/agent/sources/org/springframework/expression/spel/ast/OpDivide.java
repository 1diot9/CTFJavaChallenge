package org.springframework.expression.spel.ast;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Operation;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.util.Assert;
import org.springframework.util.NumberUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/ast/OpDivide.class */
public class OpDivide extends Operator {
    public OpDivide(int startPos, int endPos, SpelNodeImpl... operands) {
        super("/", startPos, endPos, operands);
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
        Object leftOperand = getLeftOperand().getValueInternal(state).getValue();
        Object rightOperand = getRightOperand().getValueInternal(state).getValue();
        if (leftOperand instanceof Number) {
            Number leftNumber = (Number) leftOperand;
            if (rightOperand instanceof Number) {
                Number rightNumber = (Number) rightOperand;
                if ((leftNumber instanceof BigDecimal) || (rightNumber instanceof BigDecimal)) {
                    BigDecimal leftBigDecimal = (BigDecimal) NumberUtils.convertNumberToTargetClass(leftNumber, BigDecimal.class);
                    BigDecimal rightBigDecimal = (BigDecimal) NumberUtils.convertNumberToTargetClass(rightNumber, BigDecimal.class);
                    int scale = Math.max(leftBigDecimal.scale(), rightBigDecimal.scale());
                    return new TypedValue(leftBigDecimal.divide(rightBigDecimal, scale, RoundingMode.HALF_EVEN));
                }
                if ((leftNumber instanceof Double) || (rightNumber instanceof Double)) {
                    this.exitTypeDescriptor = "D";
                    return new TypedValue(Double.valueOf(leftNumber.doubleValue() / rightNumber.doubleValue()));
                }
                if ((leftNumber instanceof Float) || (rightNumber instanceof Float)) {
                    this.exitTypeDescriptor = "F";
                    return new TypedValue(Float.valueOf(leftNumber.floatValue() / rightNumber.floatValue()));
                }
                if ((leftNumber instanceof BigInteger) || (rightNumber instanceof BigInteger)) {
                    BigInteger leftBigInteger = (BigInteger) NumberUtils.convertNumberToTargetClass(leftNumber, BigInteger.class);
                    BigInteger rightBigInteger = (BigInteger) NumberUtils.convertNumberToTargetClass(rightNumber, BigInteger.class);
                    return new TypedValue(leftBigInteger.divide(rightBigInteger));
                }
                if ((leftNumber instanceof Long) || (rightNumber instanceof Long)) {
                    this.exitTypeDescriptor = "J";
                    return new TypedValue(Long.valueOf(leftNumber.longValue() / rightNumber.longValue()));
                }
                if (CodeFlow.isIntegerForNumericOp(leftNumber) || CodeFlow.isIntegerForNumericOp(rightNumber)) {
                    this.exitTypeDescriptor = "I";
                    return new TypedValue(Integer.valueOf(leftNumber.intValue() / rightNumber.intValue()));
                }
                return new TypedValue(Double.valueOf(leftNumber.doubleValue() / rightNumber.doubleValue()));
            }
        }
        return state.operate(Operation.DIVIDE, leftOperand, rightOperand);
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public boolean isCompilable() {
        if (getLeftOperand().isCompilable()) {
            return (this.children.length <= 1 || getRightOperand().isCompilable()) && this.exitTypeDescriptor != null;
        }
        return false;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public void generateCode(MethodVisitor mv, CodeFlow cf) {
        getLeftOperand().generateCode(mv, cf);
        String leftDesc = getLeftOperand().exitTypeDescriptor;
        String exitDesc = this.exitTypeDescriptor;
        Assert.state(exitDesc != null, "No exit type descriptor");
        char targetDesc = exitDesc.charAt(0);
        CodeFlow.insertNumericUnboxOrPrimitiveTypeCoercion(mv, leftDesc, targetDesc);
        if (this.children.length > 1) {
            cf.enterCompilationScope();
            getRightOperand().generateCode(mv, cf);
            String rightDesc = getRightOperand().exitTypeDescriptor;
            cf.exitCompilationScope();
            CodeFlow.insertNumericUnboxOrPrimitiveTypeCoercion(mv, rightDesc, targetDesc);
            switch (targetDesc) {
                case 'D':
                    mv.visitInsn(Opcodes.DDIV);
                    break;
                case 'E':
                case 'G':
                case 'H':
                default:
                    throw new IllegalStateException("Unrecognized exit type descriptor: '" + this.exitTypeDescriptor + "'");
                case 'F':
                    mv.visitInsn(Opcodes.FDIV);
                    break;
                case 'I':
                    mv.visitInsn(108);
                    break;
                case 'J':
                    mv.visitInsn(Opcodes.LDIV);
                    break;
            }
        }
        cf.pushDescriptor(this.exitTypeDescriptor);
    }
}
