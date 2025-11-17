package org.apache.tomcat.websocket;

import jakarta.websocket.Extension;
import java.util.ArrayList;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/WsExtension.class */
public class WsExtension implements Extension {
    private final String name;
    private final List<Extension.Parameter> parameters = new ArrayList();

    /* JADX INFO: Access modifiers changed from: package-private */
    public WsExtension(String name) {
        this.name = name;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addParameter(Extension.Parameter parameter) {
        this.parameters.add(parameter);
    }

    @Override // jakarta.websocket.Extension
    public String getName() {
        return this.name;
    }

    @Override // jakarta.websocket.Extension
    public List<Extension.Parameter> getParameters() {
        return this.parameters;
    }
}
