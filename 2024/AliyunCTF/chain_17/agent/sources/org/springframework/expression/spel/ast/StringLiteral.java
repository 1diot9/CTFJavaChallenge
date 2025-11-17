package org.springframework.expression.spel.ast;

import org.h2.engine.Constants;
import org.springframework.asm.MethodVisitor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/ast/StringLiteral.class */
public class StringLiteral extends Literal {
    private final TypedValue value;

    public StringLiteral(String payload, int startPos, int endPos, String value) {
        super(payload, startPos, endPos);
        String valueWithinQuotes;
        char quoteCharacter = value.charAt(0);
        String valueWithinQuotes2 = value.substring(1, value.length() - 1);
        if (quoteCharacter == '\'') {
            valueWithinQuotes = StringUtils.replace(valueWithinQuotes2, Constants.CLUSTERING_DISABLED, "'");
        } else {
            valueWithinQuotes = StringUtils.replace(valueWithinQuotes2, "\"\"", "\"");
        }
        this.value = new TypedValue(valueWithinQuotes);
        this.exitTypeDescriptor = "Ljava/lang/String";
    }

    @Override // org.springframework.expression.spel.ast.Literal
    public TypedValue getLiteralValue() {
        return this.value;
    }

    @Override // org.springframework.expression.spel.ast.Literal
    public String toString() {
        String ast = String.valueOf(getLiteralValue().getValue());
        return "'" + StringUtils.replace(ast, "'", Constants.CLUSTERING_DISABLED) + "'";
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
