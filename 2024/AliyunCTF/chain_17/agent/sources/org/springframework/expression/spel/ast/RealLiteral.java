package org.springframework.expression.spel.ast;

import org.springframework.asm.MethodVisitor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;

/* loaded from: agent.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/ast/RealLiteral.class */
public class RealLiteral extends Literal {
    private final TypedValue value;

    public RealLiteral(String payload, int startPos, int endPos, double value) {
        super(payload, startPos, endPos);
        this.value = new TypedValue(Double.valueOf(value));
        this.exitTypeDescriptor = "D";
    }

    @Override // org.springframework.expression.spel.ast.Literal
    public TypedValue getLiteralValue() {
        return this.value;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public boolean isCompilable() {
        return true;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public void generateCode(MethodVisitor mv, CodeFlow cf) {
        mv.visitLdcInsn(this.value.getValue());
        cf.pushDescriptor(this.exitTypeDescriptor);
    }
}
