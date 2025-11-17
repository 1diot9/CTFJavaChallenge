package org.springframework.expression.spel.ast;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Operation;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.util.Assert;
import org.springframework.util.NumberUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/ast/OpMinus.class */
public class OpMinus extends Operator {
    public OpMinus(int startPos, int endPos, SpelNodeImpl... operands) {
        super("-", startPos, endPos, operands);
    }

    public boolean isNegativeNumberLiteral() {
        if (this.children.length == 1) {
            SpelNodeImpl spelNodeImpl = this.children[0];
            if (spelNodeImpl instanceof Literal) {
                Literal literal = (Literal) spelNodeImpl;
                if (literal.isNumberLiteral()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
        SpelNodeImpl leftOp = getLeftOperand();
        if (this.children.length < 2) {
            Object operand = leftOp.getValueInternal(state).getValue();
            if (operand instanceof Number) {
                Number number = (Number) operand;
                if (number instanceof BigDecimal) {
                    BigDecimal bigDecimal = (BigDecimal) number;
                    return new TypedValue(bigDecimal.negate());
                }
                if (number instanceof BigInteger) {
                    BigInteger bigInteger = (BigInteger) number;
                    return new TypedValue(bigInteger.negate());
                }
                if (number instanceof Double) {
                    this.exitTypeDescriptor = "D";
                    return new TypedValue(Double.valueOf(0.0d - number.doubleValue()));
                }
                if (number instanceof Float) {
                    this.exitTypeDescriptor = "F";
                    return new TypedValue(Float.valueOf(0.0f - number.floatValue()));
                }
                if (number instanceof Long) {
                    this.exitTypeDescriptor = "J";
                    return new TypedValue(Long.valueOf(0 - number.longValue()));
                }
                if (number instanceof Integer) {
                    this.exitTypeDescriptor = "I";
                    return new TypedValue(Integer.valueOf(0 - number.intValue()));
                }
                if (number instanceof Short) {
                    return new TypedValue(Integer.valueOf(0 - number.shortValue()));
                }
                if (number instanceof Byte) {
                    return new TypedValue(Integer.valueOf(0 - number.byteValue()));
                }
                return new TypedValue(Double.valueOf(0.0d - number.doubleValue()));
            }
            return state.operate(Operation.SUBTRACT, operand, null);
        }
        Object left = leftOp.getValueInternal(state).getValue();
        Object right = getRightOperand().getValueInternal(state).getValue();
        if (left instanceof Number) {
            Number leftNumber = (Number) left;
            if (right instanceof Number) {
                Number rightNumber = (Number) right;
                if ((leftNumber instanceof BigDecimal) || (rightNumber instanceof BigDecimal)) {
                    BigDecimal leftBigDecimal = (BigDecimal) NumberUtils.convertNumberToTargetClass(leftNumber, BigDecimal.class);
                    BigDecimal rightBigDecimal = (BigDecimal) NumberUtils.convertNumberToTargetClass(rightNumber, BigDecimal.class);
                    return new TypedValue(leftBigDecimal.subtract(rightBigDecimal));
                }
                if ((leftNumber instanceof Double) || (rightNumber instanceof Double)) {
                    this.exitTypeDescriptor = "D";
                    return new TypedValue(Double.valueOf(leftNumber.doubleValue() - rightNumber.doubleValue()));
                }
                if ((leftNumber instanceof Float) || (rightNumber instanceof Float)) {
                    this.exitTypeDescriptor = "F";
                    return new TypedValue(Float.valueOf(leftNumber.floatValue() - rightNumber.floatValue()));
                }
                if ((leftNumber instanceof BigInteger) || (rightNumber instanceof BigInteger)) {
                    BigInteger leftBigInteger = (BigInteger) NumberUtils.convertNumberToTargetClass(leftNumber, BigInteger.class);
                    BigInteger rightBigInteger = (BigInteger) NumberUtils.convertNumberToTargetClass(rightNumber, BigInteger.class);
                    return new TypedValue(leftBigInteger.subtract(rightBigInteger));
                }
                if ((leftNumber instanceof Long) || (rightNumber instanceof Long)) {
                    this.exitTypeDescriptor = "J";
                    return new TypedValue(Long.valueOf(leftNumber.longValue() - rightNumber.longValue()));
                }
                if (CodeFlow.isIntegerForNumericOp(leftNumber) || CodeFlow.isIntegerForNumericOp(rightNumber)) {
                    this.exitTypeDescriptor = "I";
                    return new TypedValue(Integer.valueOf(leftNumber.intValue() - rightNumber.intValue()));
                }
                return new TypedValue(Double.valueOf(leftNumber.doubleValue() - rightNumber.doubleValue()));
            }
        }
        if (left instanceof String) {
            String theString = (String) left;
            if (right instanceof Integer) {
                Integer theInteger = (Integer) right;
                if (theString.length() == 1) {
                    return new TypedValue(Character.toString((char) (theString.charAt(0) - theInteger.intValue())));
                }
            }
        }
        return state.operate(Operation.SUBTRACT, left, right);
    }

    @Override // org.springframework.expression.spel.ast.Operator, org.springframework.expression.spel.SpelNode
    public String toStringAST() {
        if (this.children.length < 2) {
            return "-" + getLeftOperand().toStringAST();
        }
        return super.toStringAST();
    }

    @Override // org.springframework.expression.spel.ast.Operator
    public SpelNodeImpl getRightOperand() {
        if (this.children.length < 2) {
            throw new IllegalStateException("No right operand");
        }
        return this.children[1];
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
                    mv.visitInsn(Opcodes.DSUB);
                    break;
                case org.springframework.asm.TypeReference.CONSTRUCTOR_REFERENCE /* 69 */:
                case org.springframework.asm.TypeReference.CAST /* 71 */:
                case 'H':
                default:
                    throw new IllegalStateException("Unrecognized exit type descriptor: '" + this.exitTypeDescriptor + "'");
                case 'F':
                    mv.visitInsn(Opcodes.FSUB);
                    break;
                case 'I':
                    mv.visitInsn(100);
                    break;
                case 'J':
                    mv.visitInsn(101);
                    break;
            }
        } else {
            switch (targetDesc) {
                case 'D':
                    mv.visitInsn(Opcodes.DNEG);
                    break;
                case org.springframework.asm.TypeReference.CONSTRUCTOR_REFERENCE /* 69 */:
                case org.springframework.asm.TypeReference.CAST /* 71 */:
                case 'H':
                default:
                    throw new IllegalStateException("Unrecognized exit type descriptor: '" + this.exitTypeDescriptor + "'");
                case 'F':
                    mv.visitInsn(Opcodes.FNEG);
                    break;
                case 'I':
                    mv.visitInsn(116);
                    break;
                case 'J':
                    mv.visitInsn(Opcodes.LNEG);
                    break;
            }
        }
        cf.pushDescriptor(this.exitTypeDescriptor);
    }
}
