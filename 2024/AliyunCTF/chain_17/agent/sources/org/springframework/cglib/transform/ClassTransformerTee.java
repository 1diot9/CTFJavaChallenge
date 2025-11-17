package org.springframework.cglib.transform;

import org.springframework.asm.ClassVisitor;
import org.springframework.cglib.core.ClassTransformer;
import org.springframework.cglib.core.Constants;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/transform/ClassTransformerTee.class */
public class ClassTransformerTee extends ClassTransformer {
    private ClassVisitor branch;

    public ClassTransformerTee(ClassVisitor branch) {
        super(Constants.ASM_API);
        this.branch = branch;
    }

    @Override // org.springframework.cglib.core.ClassTransformer
    public void setTarget(ClassVisitor target) {
        this.cv = new ClassVisitorTee(this.branch, target);
    }
}
