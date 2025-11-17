package org.apache.tomcat.websocket;

import jakarta.websocket.DeploymentException;
import jakarta.websocket.Endpoint;
import javax.naming.NamingException;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/EndpointHolder.class */
public class EndpointHolder implements ClientEndpointHolder {
    private static final StringManager sm = StringManager.getManager((Class<?>) EndpointHolder.class);
    private final Endpoint endpoint;

    public EndpointHolder(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    @Override // org.apache.tomcat.websocket.ClientEndpointHolder
    public String getClassName() {
        return this.endpoint.getClass().getName();
    }

    @Override // org.apache.tomcat.websocket.ClientEndpointHolder
    public Endpoint getInstance(InstanceManager instanceManager) throws DeploymentException {
        if (instanceManager != null) {
            try {
                instanceManager.newInstance(this.endpoint);
            } catch (ReflectiveOperationException | NamingException e) {
                throw new DeploymentException(sm.getString("clientEndpointHolder.instanceRegistrationFailed"), e);
            }
        }
        return this.endpoint;
    }
}
