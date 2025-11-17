package org.apache.tomcat.websocket;

import jakarta.websocket.Extension;
import java.util.List;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/TransformationFactory.class */
public class TransformationFactory {
    private static final TransformationFactory factory = new TransformationFactory();

    private TransformationFactory() {
    }

    public static TransformationFactory getInstance() {
        return factory;
    }

    public Transformation create(String name, List<List<Extension.Parameter>> preferences, boolean isServer) {
        if (PerMessageDeflate.NAME.equals(name)) {
            return PerMessageDeflate.negotiate(preferences, isServer);
        }
        return null;
    }
}
