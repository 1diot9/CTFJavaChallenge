package org.apache.tomcat.websocket.pojo;

import jakarta.websocket.EndpointConfig;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpointConfig;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/pojo/PojoEndpointServer.class */
public class PojoEndpointServer extends PojoEndpointBase {
    public PojoEndpointServer(Map<String, String> pathParameters, Object pojo) {
        super(pathParameters);
        setPojo(pojo);
    }

    @Override // jakarta.websocket.Endpoint
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        ServerEndpointConfig sec = (ServerEndpointConfig) endpointConfig;
        PojoMethodMapping methodMapping = (PojoMethodMapping) sec.getUserProperties().get(Constants.POJO_METHOD_MAPPING_KEY);
        setMethodMapping(methodMapping);
        doOnOpen(session, endpointConfig);
    }
}
