package org.apache.catalina;

import java.util.Set;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/DistributedManager.class */
public interface DistributedManager {
    int getActiveSessionsFull();

    Set<String> getSessionIdsFull();
}
