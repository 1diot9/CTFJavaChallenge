package org.springframework.expression.spel.ast;

import org.springframework.asm.MethodVisitor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;

/* loaded from: server.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/ast/NullLiteral.class */
public class NullLiteral extends Literal {
    public NullLiteral(int startPos, int endPos) {
        super(null, startPos, endPos);
        this.exitTypeDescriptor = "Ljava/lang/Object";
    }

    @Override // org.springframework.expression.spel.ast.Literal
    public TypedValue getLiteralValue() {
        return TypedValue.NULL;
    }

    @Override // org.springframework.expression.spel.ast.Literal
    public String toString() {
        return "null";
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public boolean isCompilable() {
        return true;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public void generateCode(MethodVisitor mv, CodeFlow cf) {
        mv.visitInsn(1);
        cf.pushDescriptor(this.exitTypeDescriptor);
    }
}
