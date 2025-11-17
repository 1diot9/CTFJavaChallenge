package org.springframework.cglib.core;

import org.springframework.asm.Type;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/core/FieldTypeCustomizer.class */
public interface FieldTypeCustomizer extends KeyFactoryCustomizer {
    void customize(CodeEmitter e, int index, Type type);

    Type getOutType(int index, Type type);
}
