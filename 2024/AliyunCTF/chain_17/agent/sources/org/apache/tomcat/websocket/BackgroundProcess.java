package org.apache.tomcat.websocket;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/BackgroundProcess.class */
public interface BackgroundProcess {
    void backgroundProcess();

    void setProcessPeriod(int i);

    int getProcessPeriod();
}
