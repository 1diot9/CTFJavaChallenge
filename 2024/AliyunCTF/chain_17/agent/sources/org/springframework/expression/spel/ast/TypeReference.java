package org.springframework.expression.spel.ast;

import java.lang.reflect.Array;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.asm.Type;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/ast/TypeReference.class */
public class TypeReference extends SpelNodeImpl {
    private final int dimensions;

    @Nullable
    private transient Class<?> type;

    public TypeReference(int startPos, int endPos, SpelNodeImpl qualifiedId) {
        this(startPos, endPos, qualifiedId, 0);
    }

    public TypeReference(int startPos, int endPos, SpelNodeImpl qualifiedId, int dims) {
        super(startPos, endPos, qualifiedId);
        this.dimensions = dims;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
        TypeCode tc;
        String typeName = (String) this.children[0].getValueInternal(state).getValue();
        Assert.state(typeName != null, "No type name");
        if (!typeName.contains(".") && Character.isLowerCase(typeName.charAt(0)) && (tc = TypeCode.valueOf(typeName.toUpperCase())) != TypeCode.OBJECT) {
            Class<?> clazz = makeArrayIfNecessary(tc.getType());
            this.exitTypeDescriptor = "Ljava/lang/Class";
            this.type = clazz;
            return new TypedValue(clazz);
        }
        Class<?> clazz2 = makeArrayIfNecessary(state.findType(typeName));
        this.exitTypeDescriptor = "Ljava/lang/Class";
        this.type = clazz2;
        return new TypedValue(clazz2);
    }

    private Class<?> makeArrayIfNecessary(Class<?> clazz) {
        if (this.dimensions < 1) {
            return clazz;
        }
        int[] dims = new int[this.dimensions];
        Object array = Array.newInstance(clazz, dims);
        return array.getClass();
    }

    @Override // org.springframework.expression.spel.SpelNode
    public String toStringAST() {
        return "T(" + getChild(0).toStringAST() + ClassUtils.ARRAY_SUFFIX.repeat(this.dimensions) + ')';
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public boolean isCompilable() {
        return this.exitTypeDescriptor != null;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public void generateCode(MethodVisitor mv, CodeFlow cf) {
        Assert.state(this.type != null, "No type available");
        if (this.type.isPrimitive()) {
            if (this.type == Boolean.TYPE) {
                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Boolean", "TYPE", "Ljava/lang/Class;");
            } else if (this.type == Byte.TYPE) {
                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Byte", "TYPE", "Ljava/lang/Class;");
            } else if (this.type == Character.TYPE) {
                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Character", "TYPE", "Ljava/lang/Class;");
            } else if (this.type == Double.TYPE) {
                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Double", "TYPE", "Ljava/lang/Class;");
            } else if (this.type == Float.TYPE) {
                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Float", "TYPE", "Ljava/lang/Class;");
            } else if (this.type == Integer.TYPE) {
                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Integer", "TYPE", "Ljava/lang/Class;");
            } else if (this.type == Long.TYPE) {
                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Long", "TYPE", "Ljava/lang/Class;");
            } else if (this.type == Short.TYPE) {
                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Short", "TYPE", "Ljava/lang/Class;");
            }
        } else {
            mv.visitLdcInsn(Type.getType(this.type));
        }
        cf.pushDescriptor(this.exitTypeDescriptor);
    }
}
