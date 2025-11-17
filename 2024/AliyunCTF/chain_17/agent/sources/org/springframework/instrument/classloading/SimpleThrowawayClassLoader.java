package org.springframework.instrument.classloading;

import org.springframework.core.OverridingClassLoader;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/instrument/classloading/SimpleThrowawayClassLoader.class */
public class SimpleThrowawayClassLoader extends OverridingClassLoader {
    static {
        ClassLoader.registerAsParallelCapable();
    }

    public SimpleThrowawayClassLoader(@Nullable ClassLoader parent) {
        super(parent);
    }
}
