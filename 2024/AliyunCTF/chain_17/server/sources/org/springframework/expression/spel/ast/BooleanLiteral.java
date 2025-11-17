package org.springframework.expression.spel.ast;

import org.springframework.asm.MethodVisitor;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.support.BooleanTypedValue;

/* loaded from: server.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/ast/BooleanLiteral.class */
public class BooleanLiteral extends Literal {
    private final BooleanTypedValue value;

    public BooleanLiteral(String payload, int startPos, int endPos, boolean value) {
        super(payload, startPos, endPos);
        this.value = BooleanTypedValue.forValue(value);
        this.exitTypeDescriptor = "Z";
    }

    @Override // org.springframework.expression.spel.ast.Literal
    public BooleanTypedValue getLiteralValue() {
        return this.value;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public boolean isCompilable() {
        return true;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public void generateCode(MethodVisitor mv, CodeFlow cf) {
        if (this.value == BooleanTypedValue.TRUE) {
            mv.visitLdcInsn(1);
        } else {
            mv.visitLdcInsn(0);
        }
        cf.pushDescriptor(this.exitTypeDescriptor);
    }
}
