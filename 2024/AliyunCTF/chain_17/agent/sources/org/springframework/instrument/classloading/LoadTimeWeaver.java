package org.springframework.instrument.classloading;

import java.lang.instrument.ClassFileTransformer;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/instrument/classloading/LoadTimeWeaver.class */
public interface LoadTimeWeaver {
    void addTransformer(ClassFileTransformer transformer);

    ClassLoader getInstrumentableClassLoader();

    ClassLoader getThrowawayClassLoader();
}
