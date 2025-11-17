package org.springframework.expression.spel;

import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.expression.PropertyAccessor;

/* loaded from: server.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/CompilablePropertyAccessor.class */
public interface CompilablePropertyAccessor extends PropertyAccessor, Opcodes {
    boolean isCompilable();

    Class<?> getPropertyType();

    void generateCode(String propertyName, MethodVisitor mv, CodeFlow cf);
}
