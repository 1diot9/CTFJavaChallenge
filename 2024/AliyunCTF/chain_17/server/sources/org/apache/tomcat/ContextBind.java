package org.apache.tomcat;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/ContextBind.class */
public interface ContextBind {
    ClassLoader bind(boolean z, ClassLoader classLoader);

    void unbind(boolean z, ClassLoader classLoader);
}
