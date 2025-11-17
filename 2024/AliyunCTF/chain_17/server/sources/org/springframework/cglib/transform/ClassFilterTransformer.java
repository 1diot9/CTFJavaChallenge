package org.springframework.cglib.transform;

import org.springframework.cglib.core.ClassTransformer;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/transform/ClassFilterTransformer.class */
public class ClassFilterTransformer extends AbstractClassFilterTransformer {
    private ClassFilter filter;

    public ClassFilterTransformer(ClassFilter filter, ClassTransformer pass) {
        super(pass);
        this.filter = filter;
    }

    @Override // org.springframework.cglib.transform.AbstractClassFilterTransformer
    protected boolean accept(int version, int access, String name, String signature, String superName, String[] interfaces) {
        return this.filter.accept(name.replace('/', '.'));
    }
}
