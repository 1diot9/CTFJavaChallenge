package org.apache.catalina;

import java.util.EventListener;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/SessionListener.class */
public interface SessionListener extends EventListener {
    void sessionEvent(SessionEvent sessionEvent);
}
