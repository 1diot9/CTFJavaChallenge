package org.springframework.instrument.classloading;

import java.lang.instrument.ClassFileTransformer;
import org.springframework.core.OverridingClassLoader;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/instrument/classloading/SimpleInstrumentableClassLoader.class */
public class SimpleInstrumentableClassLoader extends OverridingClassLoader {
    private final WeavingTransformer weavingTransformer;

    static {
        ClassLoader.registerAsParallelCapable();
    }

    public SimpleInstrumentableClassLoader(@Nullable ClassLoader parent) {
        super(parent);
        this.weavingTransformer = new WeavingTransformer(parent);
    }

    public void addTransformer(ClassFileTransformer transformer) {
        this.weavingTransformer.addTransformer(transformer);
    }

    @Override // org.springframework.core.OverridingClassLoader
    protected byte[] transformIfNecessary(String name, byte[] bytes) {
        return this.weavingTransformer.transformIfNecessary(name, bytes);
    }
}
