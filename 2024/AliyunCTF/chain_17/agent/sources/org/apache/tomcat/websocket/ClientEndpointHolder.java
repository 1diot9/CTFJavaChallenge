package org.apache.tomcat.websocket;

import jakarta.websocket.DeploymentException;
import jakarta.websocket.Endpoint;
import org.apache.tomcat.InstanceManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/ClientEndpointHolder.class */
public interface ClientEndpointHolder {
    String getClassName();

    Endpoint getInstance(InstanceManager instanceManager) throws DeploymentException;
}
