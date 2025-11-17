package org.apache.catalina;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/Engine.class */
public interface Engine extends Container {
    String getDefaultHost();

    void setDefaultHost(String str);

    String getJvmRoute();

    void setJvmRoute(String str);

    Service getService();

    void setService(Service service);
}
