package org.springframework.expression.spel.ast;

import java.lang.reflect.Modifier;
import java.util.function.Supplier;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.ast.ValueRef;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/ast/VariableReference.class */
public class VariableReference extends SpelNodeImpl {
    private static final String THIS = "this";
    private static final String ROOT = "root";
    private final String name;

    public VariableReference(String variableName, int startPos, int endPos) {
        super(startPos, endPos, new SpelNodeImpl[0]);
        this.name = variableName;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public ValueRef getValueRef(ExpressionState state) throws SpelEvaluationException {
        if (this.name.equals(THIS)) {
            return new ValueRef.TypedValueHolderValueRef(state.getActiveContextObject(), this);
        }
        if (this.name.equals(ROOT)) {
            return new ValueRef.TypedValueHolderValueRef(state.getRootContextObject(), this);
        }
        TypedValue result = state.lookupVariable(this.name);
        return new VariableRef(this.name, result, state.getEvaluationContext());
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public TypedValue getValueInternal(ExpressionState state) throws SpelEvaluationException {
        if (this.name.equals(THIS)) {
            return state.getActiveContextObject();
        }
        if (this.name.equals(ROOT)) {
            TypedValue result = state.getRootContextObject();
            this.exitTypeDescriptor = CodeFlow.toDescriptorFromObject(result.getValue());
            return result;
        }
        TypedValue result2 = state.lookupVariable(this.name);
        Object value = result2.getValue();
        if (value == null || !Modifier.isPublic(value.getClass().getModifiers())) {
            this.exitTypeDescriptor = "Ljava/lang/Object";
        } else {
            this.exitTypeDescriptor = CodeFlow.toDescriptorFromObject(value);
        }
        return result2;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public TypedValue setValueInternal(ExpressionState state, Supplier<TypedValue> valueSupplier) throws EvaluationException {
        return state.assignVariable(this.name, valueSupplier);
    }

    @Override // org.springframework.expression.spel.SpelNode
    public String toStringAST() {
        return "#" + this.name;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl, org.springframework.expression.spel.SpelNode
    public boolean isWritable(ExpressionState expressionState) throws SpelEvaluationException {
        return (this.name.equals(THIS) || this.name.equals(ROOT)) ? false : true;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public boolean isCompilable() {
        return this.exitTypeDescriptor != null;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public void generateCode(MethodVisitor mv, CodeFlow cf) {
        if (this.name.equals(ROOT)) {
            mv.visitVarInsn(25, 1);
        } else {
            mv.visitVarInsn(25, 2);
            mv.visitLdcInsn(this.name);
            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "org/springframework/expression/EvaluationContext", "lookupVariable", "(Ljava/lang/String;)Ljava/lang/Object;", true);
        }
        CodeFlow.insertCheckCast(mv, this.exitTypeDescriptor);
        cf.pushDescriptor(this.exitTypeDescriptor);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/ast/VariableReference$VariableRef.class */
    private static class VariableRef implements ValueRef {
        private final String name;
        private final TypedValue value;
        private final EvaluationContext evaluationContext;

        public VariableRef(String name, TypedValue value, EvaluationContext evaluationContext) {
            this.name = name;
            this.value = value;
            this.evaluationContext = evaluationContext;
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public TypedValue getValue() {
            return this.value;
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public void setValue(@Nullable Object newValue) {
            this.evaluationContext.setVariable(this.name, newValue);
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public boolean isWritable() {
            return true;
        }
    }
}
