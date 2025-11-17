package org.apache.catalina;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/SessionIdGenerator.class */
public interface SessionIdGenerator {
    String getJvmRoute();

    void setJvmRoute(String str);

    int getSessionIdLength();

    void setSessionIdLength(int i);

    String generateSessionId();

    String generateSessionId(String str);
}
