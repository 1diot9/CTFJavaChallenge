package org.springframework.cglib.transform;

import org.springframework.asm.ClassVisitor;
import org.springframework.cglib.core.ClassTransformer;
import org.springframework.cglib.core.Constants;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/transform/AbstractClassTransformer.class */
public abstract class AbstractClassTransformer extends ClassTransformer {
    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractClassTransformer() {
        super(Constants.ASM_API);
    }

    @Override // org.springframework.cglib.core.ClassTransformer
    public void setTarget(ClassVisitor target) {
        this.cv = target;
    }
}
