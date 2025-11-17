package org.springframework.expression.spel.ast;

import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;

/* loaded from: server.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/ast/Identifier.class */
public class Identifier extends SpelNodeImpl {
    private final TypedValue id;

    public Identifier(String payload, int startPos, int endPos) {
        super(startPos, endPos, new SpelNodeImpl[0]);
        this.id = new TypedValue(payload);
    }

    @Override // org.springframework.expression.spel.SpelNode
    public String toStringAST() {
        return String.valueOf(this.id.getValue());
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public TypedValue getValueInternal(ExpressionState state) {
        return this.id;
    }
}
