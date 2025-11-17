package org.springframework.cglib.transform;

import org.springframework.asm.ClassVisitor;
import org.springframework.cglib.core.ClassGenerator;
import org.springframework.cglib.core.ClassTransformer;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/transform/TransformingClassGenerator.class */
public class TransformingClassGenerator implements ClassGenerator {
    private final ClassGenerator gen;
    private final ClassTransformer t;

    public TransformingClassGenerator(ClassGenerator gen, ClassTransformer t) {
        this.gen = gen;
        this.t = t;
    }

    @Override // org.springframework.cglib.core.ClassGenerator
    public void generateClass(ClassVisitor v) throws Exception {
        this.t.setTarget(v);
        this.gen.generateClass(this.t);
    }
}
