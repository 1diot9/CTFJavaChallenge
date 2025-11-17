package org.springframework.expression.spel.ast;

import org.springframework.asm.Label;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/ast/Elvis.class */
public class Elvis extends SpelNodeImpl {
    public Elvis(int startPos, int endPos, SpelNodeImpl... args) {
        super(startPos, endPos, args);
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
        TypedValue value = this.children[0].getValueInternal(state);
        if (value.getValue() != null && !"".equals(value.getValue())) {
            return value;
        }
        TypedValue result = this.children[1].getValueInternal(state);
        computeExitTypeDescriptor();
        return result;
    }

    @Override // org.springframework.expression.spel.SpelNode
    public String toStringAST() {
        return "(" + getChild(0).toStringAST() + " ?: " + getChild(1).toStringAST() + ")";
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public boolean isCompilable() {
        SpelNodeImpl condition = this.children[0];
        SpelNodeImpl ifNullValue = this.children[1];
        return condition.isCompilable() && ifNullValue.isCompilable() && condition.exitTypeDescriptor != null && ifNullValue.exitTypeDescriptor != null;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public void generateCode(MethodVisitor mv, CodeFlow cf) {
        computeExitTypeDescriptor();
        cf.enterCompilationScope();
        this.children[0].generateCode(mv, cf);
        String lastDesc = cf.lastDescriptor();
        Assert.state(lastDesc != null, "No last descriptor");
        CodeFlow.insertBoxIfNecessary(mv, lastDesc.charAt(0));
        cf.exitCompilationScope();
        Label elseTarget = new Label();
        Label endOfIf = new Label();
        mv.visitInsn(89);
        mv.visitJumpInsn(Opcodes.IFNULL, elseTarget);
        mv.visitInsn(89);
        mv.visitLdcInsn("");
        mv.visitInsn(95);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
        mv.visitJumpInsn(153, endOfIf);
        mv.visitLabel(elseTarget);
        mv.visitInsn(87);
        cf.enterCompilationScope();
        this.children[1].generateCode(mv, cf);
        if (!CodeFlow.isPrimitive(this.exitTypeDescriptor)) {
            String lastDesc2 = cf.lastDescriptor();
            Assert.state(lastDesc2 != null, "No last descriptor");
            CodeFlow.insertBoxIfNecessary(mv, lastDesc2.charAt(0));
        }
        cf.exitCompilationScope();
        mv.visitLabel(endOfIf);
        cf.pushDescriptor(this.exitTypeDescriptor);
    }

    private void computeExitTypeDescriptor() {
        if (this.exitTypeDescriptor == null && this.children[0].exitTypeDescriptor != null && this.children[1].exitTypeDescriptor != null) {
            String conditionDescriptor = this.children[0].exitTypeDescriptor;
            String ifNullValueDescriptor = this.children[1].exitTypeDescriptor;
            if (ObjectUtils.nullSafeEquals(conditionDescriptor, ifNullValueDescriptor)) {
                this.exitTypeDescriptor = conditionDescriptor;
            } else {
                this.exitTypeDescriptor = "Ljava/lang/Object";
            }
        }
    }
}
