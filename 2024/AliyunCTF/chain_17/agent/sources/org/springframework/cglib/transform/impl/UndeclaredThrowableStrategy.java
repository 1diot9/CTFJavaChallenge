package org.springframework.cglib.transform.impl;

import org.springframework.cglib.core.ClassGenerator;
import org.springframework.cglib.core.ClassTransformer;
import org.springframework.cglib.core.DefaultGeneratorStrategy;
import org.springframework.cglib.core.TypeUtils;
import org.springframework.cglib.transform.MethodFilter;
import org.springframework.cglib.transform.MethodFilterTransformer;
import org.springframework.cglib.transform.TransformingClassGenerator;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/transform/impl/UndeclaredThrowableStrategy.class */
public class UndeclaredThrowableStrategy extends DefaultGeneratorStrategy {
    private Class wrapper;
    private static final MethodFilter TRANSFORM_FILTER = (access, name, desc, signature, exceptions) -> {
        return !TypeUtils.isPrivate(access) && name.indexOf(36) < 0;
    };

    public UndeclaredThrowableStrategy(Class wrapper) {
        this.wrapper = wrapper;
    }

    @Override // org.springframework.cglib.core.DefaultGeneratorStrategy
    protected ClassGenerator transform(ClassGenerator cg) throws Exception {
        ClassTransformer tr = new UndeclaredThrowableTransformer(this.wrapper);
        return new TransformingClassGenerator(cg, new MethodFilterTransformer(TRANSFORM_FILTER, tr));
    }
}
