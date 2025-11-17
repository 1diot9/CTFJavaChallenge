package org.apache.tomcat.websocket;

import jakarta.websocket.Extension;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/WsExtensionParameter.class */
public class WsExtensionParameter implements Extension.Parameter {
    private final String name;
    private final String value;

    /* JADX INFO: Access modifiers changed from: package-private */
    public WsExtensionParameter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override // jakarta.websocket.Extension.Parameter
    public String getName() {
        return this.name;
    }

    @Override // jakarta.websocket.Extension.Parameter
    public String getValue() {
        return this.value;
    }
}
