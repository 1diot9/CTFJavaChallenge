package org.apache.catalina;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/ContainerServlet.class */
public interface ContainerServlet {
    Wrapper getWrapper();

    void setWrapper(Wrapper wrapper);
}
