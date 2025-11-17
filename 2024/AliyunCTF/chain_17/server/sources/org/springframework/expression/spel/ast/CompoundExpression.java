package org.springframework.expression.spel.ast;

import java.util.function.Supplier;
import org.springframework.asm.MethodVisitor;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelEvaluationException;

/* loaded from: server.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/ast/CompoundExpression.class */
public class CompoundExpression extends SpelNodeImpl {
    public CompoundExpression(int startPos, int endPos, SpelNodeImpl... expressionComponents) {
        super(startPos, endPos, expressionComponents);
        if (expressionComponents.length < 2) {
            throw new IllegalStateException("Do not build compound expressions with less than two entries: " + expressionComponents.length);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public ValueRef getValueRef(ExpressionState state) throws EvaluationException {
        if (getChildCount() == 1) {
            return this.children[0].getValueRef(state);
        }
        SpelNodeImpl nextNode = this.children[0];
        try {
            TypedValue result = nextNode.getValueInternal(state);
            int cc = getChildCount();
            for (int i = 1; i < cc - 1; i++) {
                try {
                    state.pushActiveContextObject(result);
                    nextNode = this.children[i];
                    result = nextNode.getValueInternal(state);
                    state.popActiveContextObject();
                } finally {
                }
            }
            try {
                state.pushActiveContextObject(result);
                nextNode = this.children[cc - 1];
                ValueRef valueRef = nextNode.getValueRef(state);
                state.popActiveContextObject();
                return valueRef;
            } finally {
            }
        } catch (SpelEvaluationException ex) {
            ex.setPosition(nextNode.getStartPosition());
            throw ex;
        }
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
        ValueRef ref = getValueRef(state);
        TypedValue result = ref.getValue();
        this.exitTypeDescriptor = this.children[this.children.length - 1].exitTypeDescriptor;
        return result;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public TypedValue setValueInternal(ExpressionState state, Supplier<TypedValue> valueSupplier) throws EvaluationException {
        TypedValue typedValue = valueSupplier.get();
        getValueRef(state).setValue(typedValue.getValue());
        return typedValue;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl, org.springframework.expression.spel.SpelNode
    public boolean isWritable(ExpressionState state) throws EvaluationException {
        return getValueRef(state).isWritable();
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x004c, code lost:            if (r0.isNullSafe() == false) goto L13;     */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x0064, code lost:            r0.append('?');     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x0061, code lost:            if (r0.isNullSafe() != false) goto L17;     */
    @Override // org.springframework.expression.spel.SpelNode
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.lang.String toStringAST() {
        /*
            r4 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r1 = r0
            r1.<init>()
            r5 = r0
            r0 = 0
            r6 = r0
        La:
            r0 = r6
            r1 = r4
            int r1 = r1.getChildCount()
            if (r0 >= r1) goto L78
            r0 = r5
            r1 = r4
            r2 = r6
            org.springframework.expression.spel.SpelNode r1 = r1.getChild(r2)
            java.lang.String r1 = r1.toStringAST()
            java.lang.StringBuilder r0 = r0.append(r1)
            r0 = r6
            r1 = r4
            int r1 = r1.getChildCount()
            r2 = 1
            int r1 = r1 - r2
            if (r0 >= r1) goto L72
            r0 = r4
            r1 = r6
            r2 = 1
            int r1 = r1 + r2
            org.springframework.expression.spel.SpelNode r0 = r0.getChild(r1)
            r7 = r0
            r0 = r7
            boolean r0 = r0 instanceof org.springframework.expression.spel.ast.Indexer
            if (r0 != 0) goto L72
            r0 = r7
            boolean r0 = r0 instanceof org.springframework.expression.spel.ast.MethodReference
            if (r0 == 0) goto L4f
            r0 = r7
            org.springframework.expression.spel.ast.MethodReference r0 = (org.springframework.expression.spel.ast.MethodReference) r0
            r8 = r0
            r0 = r8
            boolean r0 = r0.isNullSafe()
            if (r0 != 0) goto L64
        L4f:
            r0 = r7
            boolean r0 = r0 instanceof org.springframework.expression.spel.ast.PropertyOrFieldReference
            if (r0 == 0) goto L6b
            r0 = r7
            org.springframework.expression.spel.ast.PropertyOrFieldReference r0 = (org.springframework.expression.spel.ast.PropertyOrFieldReference) r0
            r9 = r0
            r0 = r9
            boolean r0 = r0.isNullSafe()
            if (r0 == 0) goto L6b
        L64:
            r0 = r5
            r1 = 63
            java.lang.StringBuilder r0 = r0.append(r1)
        L6b:
            r0 = r5
            r1 = 46
            java.lang.StringBuilder r0 = r0.append(r1)
        L72:
            int r6 = r6 + 1
            goto La
        L78:
            r0 = r5
            java.lang.String r0 = r0.toString()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.expression.spel.ast.CompoundExpression.toStringAST():java.lang.String");
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public boolean isCompilable() {
        for (SpelNodeImpl child : this.children) {
            if (!child.isCompilable()) {
                return false;
            }
        }
        return true;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public void generateCode(MethodVisitor mv, CodeFlow cf) {
        for (SpelNodeImpl child : this.children) {
            child.generateCode(mv, cf);
        }
        cf.pushDescriptor(this.exitTypeDescriptor);
    }
}
