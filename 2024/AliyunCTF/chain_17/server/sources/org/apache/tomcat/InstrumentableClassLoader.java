package org.apache.tomcat;

import java.lang.instrument.ClassFileTransformer;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/InstrumentableClassLoader.class */
public interface InstrumentableClassLoader {
    void addTransformer(ClassFileTransformer classFileTransformer);

    void removeTransformer(ClassFileTransformer classFileTransformer);

    ClassLoader copyWithoutTransformers();
}
