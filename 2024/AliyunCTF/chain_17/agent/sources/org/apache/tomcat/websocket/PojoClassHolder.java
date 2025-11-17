package org.apache.tomcat.websocket;

import jakarta.websocket.ClientEndpointConfig;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.Endpoint;
import javax.naming.NamingException;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.pojo.PojoEndpointClient;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/PojoClassHolder.class */
public class PojoClassHolder implements ClientEndpointHolder {
    private static final StringManager sm = StringManager.getManager((Class<?>) PojoClassHolder.class);
    private final Class<?> pojoClazz;
    private final ClientEndpointConfig clientEndpointConfig;

    public PojoClassHolder(Class<?> pojoClazz, ClientEndpointConfig clientEndpointConfig) {
        this.pojoClazz = pojoClazz;
        this.clientEndpointConfig = clientEndpointConfig;
    }

    @Override // org.apache.tomcat.websocket.ClientEndpointHolder
    public String getClassName() {
        return this.pojoClazz.getName();
    }

    @Override // org.apache.tomcat.websocket.ClientEndpointHolder
    public Endpoint getInstance(InstanceManager instanceManager) throws DeploymentException {
        Object pojo;
        try {
            if (instanceManager == null) {
                pojo = this.pojoClazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            } else {
                pojo = instanceManager.newInstance(this.pojoClazz);
            }
            return new PojoEndpointClient(pojo, this.clientEndpointConfig.getDecoders(), instanceManager);
        } catch (ReflectiveOperationException | SecurityException | NamingException e) {
            throw new DeploymentException(sm.getString("clientEndpointHolder.instanceCreationFailed"), e);
        }
    }
}
