package org.springframework.cglib.core;

import org.springframework.asm.ClassWriter;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/core/DefaultGeneratorStrategy.class */
public class DefaultGeneratorStrategy implements GeneratorStrategy {
    public static final DefaultGeneratorStrategy INSTANCE = new DefaultGeneratorStrategy();

    @Override // org.springframework.cglib.core.GeneratorStrategy
    public byte[] generate(ClassGenerator cg) throws Exception {
        DebuggingClassWriter cw = getClassVisitor();
        transform(cg).generateClass(cw);
        return transform(cw.toByteArray());
    }

    protected DebuggingClassWriter getClassVisitor() throws Exception {
        return new DebuggingClassWriter(2);
    }

    protected final ClassWriter getClassWriter() {
        throw new UnsupportedOperationException("You are calling getClassWriter, which no longer exists in this cglib version.");
    }

    protected byte[] transform(byte[] b) throws Exception {
        return b;
    }

    protected ClassGenerator transform(ClassGenerator cg) throws Exception {
        return cg;
    }
}
