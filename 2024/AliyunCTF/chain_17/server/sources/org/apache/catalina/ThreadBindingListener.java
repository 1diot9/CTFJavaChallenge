package org.apache.catalina;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/ThreadBindingListener.class */
public interface ThreadBindingListener {
    void bind();

    void unbind();
}
