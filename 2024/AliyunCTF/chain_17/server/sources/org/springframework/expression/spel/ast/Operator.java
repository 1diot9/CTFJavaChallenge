package org.springframework.expression.spel.ast;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.springframework.asm.Label;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.lang.Nullable;
import org.springframework.util.NumberUtils;
import org.springframework.util.ObjectUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/ast/Operator.class */
public abstract class Operator extends SpelNodeImpl {
    private final String operatorName;

    @Nullable
    protected String leftActualDescriptor;

    @Nullable
    protected String rightActualDescriptor;

    public Operator(String payload, int startPos, int endPos, SpelNodeImpl... operands) {
        super(startPos, endPos, operands);
        this.operatorName = payload;
    }

    public SpelNodeImpl getLeftOperand() {
        return this.children[0];
    }

    public SpelNodeImpl getRightOperand() {
        return this.children[1];
    }

    public final String getOperatorName() {
        return this.operatorName;
    }

    @Override // org.springframework.expression.spel.SpelNode
    public String toStringAST() {
        StringBuilder sb = new StringBuilder("(");
        sb.append(getChild(0).toStringAST());
        for (int i = 1; i < getChildCount(); i++) {
            sb.append(' ').append(getOperatorName()).append(' ');
            sb.append(getChild(i).toStringAST());
        }
        sb.append(')');
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isCompilableOperatorUsingNumerics() {
        SpelNodeImpl left = getLeftOperand();
        SpelNodeImpl right = getRightOperand();
        if (!left.isCompilable() || !right.isCompilable()) {
            return false;
        }
        String leftDesc = left.exitTypeDescriptor;
        String rightDesc = right.exitTypeDescriptor;
        DescriptorComparison dc = DescriptorComparison.checkNumericCompatibility(leftDesc, rightDesc, this.leftActualDescriptor, this.rightActualDescriptor);
        return dc.areNumbers && dc.areCompatible;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void generateComparisonCode(MethodVisitor mv, CodeFlow cf, int compInstruction1, int compInstruction2) {
        SpelNodeImpl left = getLeftOperand();
        SpelNodeImpl right = getRightOperand();
        String leftDesc = left.exitTypeDescriptor;
        String rightDesc = right.exitTypeDescriptor;
        Label elseTarget = new Label();
        Label endOfIf = new Label();
        boolean unboxLeft = !CodeFlow.isPrimitive(leftDesc);
        boolean unboxRight = !CodeFlow.isPrimitive(rightDesc);
        DescriptorComparison dc = DescriptorComparison.checkNumericCompatibility(leftDesc, rightDesc, this.leftActualDescriptor, this.rightActualDescriptor);
        char targetType = dc.compatibleType;
        cf.enterCompilationScope();
        left.generateCode(mv, cf);
        cf.exitCompilationScope();
        if (CodeFlow.isPrimitive(leftDesc)) {
            CodeFlow.insertBoxIfNecessary(mv, leftDesc);
            unboxLeft = true;
        }
        cf.enterCompilationScope();
        right.generateCode(mv, cf);
        cf.exitCompilationScope();
        if (CodeFlow.isPrimitive(rightDesc)) {
            CodeFlow.insertBoxIfNecessary(mv, rightDesc);
            unboxRight = true;
        }
        Label rightIsNonNull = new Label();
        mv.visitInsn(89);
        mv.visitJumpInsn(Opcodes.IFNONNULL, rightIsNonNull);
        mv.visitInsn(95);
        Label leftNotNullRightIsNull = new Label();
        mv.visitJumpInsn(Opcodes.IFNONNULL, leftNotNullRightIsNull);
        mv.visitInsn(87);
        switch (compInstruction1) {
            case 155:
            case 157:
                mv.visitInsn(4);
                break;
            case 156:
            case 158:
                mv.visitInsn(3);
                break;
            default:
                throw new IllegalStateException("Unsupported: " + compInstruction1);
        }
        mv.visitJumpInsn(Opcodes.GOTO, endOfIf);
        mv.visitLabel(leftNotNullRightIsNull);
        mv.visitInsn(87);
        switch (compInstruction1) {
            case 155:
            case 158:
                mv.visitInsn(4);
                break;
            case 156:
            case 157:
                mv.visitInsn(3);
                break;
            default:
                throw new IllegalStateException("Unsupported: " + compInstruction1);
        }
        mv.visitJumpInsn(Opcodes.GOTO, endOfIf);
        mv.visitLabel(rightIsNonNull);
        mv.visitInsn(95);
        mv.visitInsn(89);
        Label neitherRightNorLeftAreNull = new Label();
        mv.visitJumpInsn(Opcodes.IFNONNULL, neitherRightNorLeftAreNull);
        mv.visitInsn(88);
        switch (compInstruction1) {
            case 155:
            case 158:
                mv.visitInsn(3);
                break;
            case 156:
            case 157:
                mv.visitInsn(4);
                break;
            default:
                throw new IllegalStateException("Unsupported: " + compInstruction1);
        }
        mv.visitJumpInsn(Opcodes.GOTO, endOfIf);
        mv.visitLabel(neitherRightNorLeftAreNull);
        if (unboxLeft) {
            CodeFlow.insertUnboxInsns(mv, targetType, leftDesc);
        }
        if (targetType == 'D' || targetType == 'J') {
            mv.visitInsn(93);
            mv.visitInsn(88);
        } else {
            mv.visitInsn(95);
        }
        if (unboxRight) {
            CodeFlow.insertUnboxInsns(mv, targetType, rightDesc);
        }
        if (targetType == 'D') {
            mv.visitInsn(152);
            mv.visitJumpInsn(compInstruction1, elseTarget);
        } else if (targetType == 'F') {
            mv.visitInsn(150);
            mv.visitJumpInsn(compInstruction1, elseTarget);
        } else if (targetType == 'J') {
            mv.visitInsn(Opcodes.LCMP);
            mv.visitJumpInsn(compInstruction1, elseTarget);
        } else if (targetType == 'I') {
            mv.visitJumpInsn(compInstruction2, elseTarget);
        } else {
            throw new IllegalStateException("Unexpected descriptor " + leftDesc);
        }
        mv.visitInsn(4);
        mv.visitJumpInsn(Opcodes.GOTO, endOfIf);
        mv.visitLabel(elseTarget);
        mv.visitInsn(3);
        mv.visitLabel(endOfIf);
        cf.pushDescriptor("Z");
    }

    public static boolean equalityCheck(EvaluationContext context, @Nullable Object left, @Nullable Object right) {
        if (left instanceof Number) {
            Number leftNumber = (Number) left;
            if (right instanceof Number) {
                Number rightNumber = (Number) right;
                if ((leftNumber instanceof BigDecimal) || (rightNumber instanceof BigDecimal)) {
                    BigDecimal leftBigDecimal = (BigDecimal) NumberUtils.convertNumberToTargetClass(leftNumber, BigDecimal.class);
                    BigDecimal rightBigDecimal = (BigDecimal) NumberUtils.convertNumberToTargetClass(rightNumber, BigDecimal.class);
                    return leftBigDecimal.compareTo(rightBigDecimal) == 0;
                }
                if ((leftNumber instanceof Double) || (rightNumber instanceof Double)) {
                    return leftNumber.doubleValue() == rightNumber.doubleValue();
                }
                if ((leftNumber instanceof Float) || (rightNumber instanceof Float)) {
                    return leftNumber.floatValue() == rightNumber.floatValue();
                }
                if (!(leftNumber instanceof BigInteger) && !(rightNumber instanceof BigInteger)) {
                    return ((leftNumber instanceof Long) || (rightNumber instanceof Long)) ? leftNumber.longValue() == rightNumber.longValue() : ((leftNumber instanceof Integer) || (rightNumber instanceof Integer)) ? leftNumber.intValue() == rightNumber.intValue() : ((leftNumber instanceof Short) || (rightNumber instanceof Short)) ? leftNumber.shortValue() == rightNumber.shortValue() : ((leftNumber instanceof Byte) || (rightNumber instanceof Byte)) ? leftNumber.byteValue() == rightNumber.byteValue() : leftNumber.doubleValue() == rightNumber.doubleValue();
                }
                BigInteger leftBigInteger = (BigInteger) NumberUtils.convertNumberToTargetClass(leftNumber, BigInteger.class);
                BigInteger rightBigInteger = (BigInteger) NumberUtils.convertNumberToTargetClass(rightNumber, BigInteger.class);
                return leftBigInteger.compareTo(rightBigInteger) == 0;
            }
        }
        if ((left instanceof CharSequence) && (right instanceof CharSequence)) {
            return left.toString().equals(right.toString());
        }
        if ((left instanceof Boolean) && (right instanceof Boolean)) {
            return left.equals(right);
        }
        if (ObjectUtils.nullSafeEquals(left, right)) {
            return true;
        }
        return context.getTypeComparator().canCompare(left, right) && context.getTypeComparator().compare(left, right) == 0;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/ast/Operator$DescriptorComparison.class */
    protected static final class DescriptorComparison {
        static final DescriptorComparison NOT_NUMBERS = new DescriptorComparison(false, false, ' ');
        static final DescriptorComparison INCOMPATIBLE_NUMBERS = new DescriptorComparison(true, false, ' ');
        final boolean areNumbers;
        final boolean areCompatible;
        final char compatibleType;

        private DescriptorComparison(boolean areNumbers, boolean areCompatible, char compatibleType) {
            this.areNumbers = areNumbers;
            this.areCompatible = areCompatible;
            this.compatibleType = compatibleType;
        }

        public static DescriptorComparison checkNumericCompatibility(@Nullable String leftDeclaredDescriptor, @Nullable String rightDeclaredDescriptor, @Nullable String leftActualDescriptor, @Nullable String rightActualDescriptor) {
            String ld = leftDeclaredDescriptor;
            String rd = rightDeclaredDescriptor;
            boolean leftNumeric = CodeFlow.isPrimitiveOrUnboxableSupportedNumberOrBoolean(ld);
            boolean rightNumeric = CodeFlow.isPrimitiveOrUnboxableSupportedNumberOrBoolean(rd);
            if (!leftNumeric && !ObjectUtils.nullSafeEquals(ld, leftActualDescriptor)) {
                ld = leftActualDescriptor;
                leftNumeric = CodeFlow.isPrimitiveOrUnboxableSupportedNumberOrBoolean(ld);
            }
            if (!rightNumeric && !ObjectUtils.nullSafeEquals(rd, rightActualDescriptor)) {
                rd = rightActualDescriptor;
                rightNumeric = CodeFlow.isPrimitiveOrUnboxableSupportedNumberOrBoolean(rd);
            }
            if (leftNumeric && rightNumeric) {
                if (CodeFlow.areBoxingCompatible(ld, rd)) {
                    return new DescriptorComparison(true, true, CodeFlow.toPrimitiveTargetDesc(ld));
                }
                return INCOMPATIBLE_NUMBERS;
            }
            return NOT_NUMBERS;
        }
    }
}
