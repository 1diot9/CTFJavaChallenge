package org.apache.tomcat.websocket.pojo;

import jakarta.websocket.Decoder;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.Session;
import java.util.Collections;
import java.util.List;
import org.apache.tomcat.InstanceManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/pojo/PojoEndpointClient.class */
public class PojoEndpointClient extends PojoEndpointBase {
    public PojoEndpointClient(Object pojo, List<Class<? extends Decoder>> decoders, InstanceManager instanceManager) throws DeploymentException {
        super(Collections.emptyMap());
        setPojo(pojo);
        setMethodMapping(new PojoMethodMapping(pojo.getClass(), decoders, null, instanceManager));
    }

    @Override // jakarta.websocket.Endpoint
    public void onOpen(Session session, EndpointConfig config) {
        doOnOpen(session, config);
    }
}
