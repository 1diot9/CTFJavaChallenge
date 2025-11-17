package org.apache.catalina;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/StoreManager.class */
public interface StoreManager extends DistributedManager {
    Store getStore();

    void removeSuper(Session session);
}
