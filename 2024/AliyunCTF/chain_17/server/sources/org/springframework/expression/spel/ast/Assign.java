package org.springframework.expression.spel.ast;

import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;

/* loaded from: server.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/ast/Assign.class */
public class Assign extends SpelNodeImpl {
    public Assign(int startPos, int endPos, SpelNodeImpl... operands) {
        super(startPos, endPos, operands);
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
        return this.children[0].setValueInternal(state, () -> {
            return this.children[1].getValueInternal(state);
        });
    }

    @Override // org.springframework.expression.spel.SpelNode
    public String toStringAST() {
        return getChild(0).toStringAST() + "=" + getChild(1).toStringAST();
    }
}
