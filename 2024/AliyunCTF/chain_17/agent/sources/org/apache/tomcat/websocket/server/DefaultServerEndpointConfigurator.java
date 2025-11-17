package org.apache.tomcat.websocket.server;

import aQute.bnd.annotation.spi.ServiceProvider;
import jakarta.websocket.Extension;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ServiceProvider(ServerEndpointConfig.Configurator.class)
/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/server/DefaultServerEndpointConfigurator.class */
public class DefaultServerEndpointConfigurator extends ServerEndpointConfig.Configurator {
    @Override // jakarta.websocket.server.ServerEndpointConfig.Configurator
    public <T> T getEndpointInstance(Class<T> clazz) throws InstantiationException {
        try {
            return clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (InstantiationException e) {
            throw e;
        } catch (ReflectiveOperationException e2) {
            InstantiationException ie = new InstantiationException();
            ie.initCause(e2);
            throw ie;
        }
    }

    @Override // jakarta.websocket.server.ServerEndpointConfig.Configurator
    public String getNegotiatedSubprotocol(List<String> supported, List<String> requested) {
        for (String request : requested) {
            if (supported.contains(request)) {
                return request;
            }
        }
        return "";
    }

    @Override // jakarta.websocket.server.ServerEndpointConfig.Configurator
    public List<Extension> getNegotiatedExtensions(List<Extension> installed, List<Extension> requested) {
        Set<String> installedNames = new HashSet<>();
        for (Extension e : installed) {
            installedNames.add(e.getName());
        }
        List<Extension> result = new ArrayList<>();
        for (Extension request : requested) {
            if (installedNames.contains(request.getName())) {
                result.add(request);
            }
        }
        return result;
    }

    @Override // jakarta.websocket.server.ServerEndpointConfig.Configurator
    public boolean checkOrigin(String originHeaderValue) {
        return true;
    }

    @Override // jakarta.websocket.server.ServerEndpointConfig.Configurator
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
    }
}
