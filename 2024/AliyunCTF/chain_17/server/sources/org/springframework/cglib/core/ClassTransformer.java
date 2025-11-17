package org.springframework.cglib.core;

import org.springframework.asm.ClassVisitor;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/core/ClassTransformer.class */
public abstract class ClassTransformer extends ClassVisitor {
    public abstract void setTarget(ClassVisitor target);

    public ClassTransformer() {
        super(Constants.ASM_API);
    }

    public ClassTransformer(int opcode) {
        super(opcode);
    }
}
