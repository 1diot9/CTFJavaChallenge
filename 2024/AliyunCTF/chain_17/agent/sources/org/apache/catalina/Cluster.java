package org.apache.catalina;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/Cluster.class */
public interface Cluster extends Contained {
    String getClusterName();

    void setClusterName(String str);

    Manager createManager(String str);

    void registerManager(Manager manager);

    void removeManager(Manager manager);

    void backgroundProcess();
}
