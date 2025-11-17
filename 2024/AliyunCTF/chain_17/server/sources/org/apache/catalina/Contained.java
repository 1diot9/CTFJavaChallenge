package org.apache.catalina;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/Contained.class */
public interface Contained {
    Container getContainer();

    void setContainer(Container container);
}
