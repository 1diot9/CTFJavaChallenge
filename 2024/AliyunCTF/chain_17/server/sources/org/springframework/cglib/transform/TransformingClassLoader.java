package org.springframework.cglib.transform;

import org.springframework.asm.ClassReader;
import org.springframework.cglib.core.ClassGenerator;
import org.springframework.cglib.core.ClassTransformer;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/transform/TransformingClassLoader.class */
public class TransformingClassLoader extends AbstractClassLoader {
    private final ClassTransformerFactory t;

    public TransformingClassLoader(ClassLoader parent, ClassFilter filter, ClassTransformerFactory t) {
        super(parent, parent, filter);
        this.t = t;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.cglib.transform.AbstractClassLoader
    public ClassGenerator getGenerator(ClassReader r) {
        ClassTransformer t2 = this.t.newInstance();
        return new TransformingClassGenerator(super.getGenerator(r), t2);
    }
}
