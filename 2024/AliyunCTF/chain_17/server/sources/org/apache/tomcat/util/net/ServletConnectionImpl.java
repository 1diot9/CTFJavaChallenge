package org.apache.tomcat.util.net;

import jakarta.servlet.ServletConnection;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/ServletConnectionImpl.class */
public class ServletConnectionImpl implements ServletConnection {
    private final String connectionId;
    private final String protocol;
    private final String protocolConnectionId;
    private final boolean secure;

    public ServletConnectionImpl(String connectionId, String protocol, String protocolConnectionId, boolean secure) {
        this.connectionId = connectionId;
        this.protocol = protocol;
        this.protocolConnectionId = protocolConnectionId;
        this.secure = secure;
    }

    @Override // jakarta.servlet.ServletConnection
    public String getConnectionId() {
        return this.connectionId;
    }

    @Override // jakarta.servlet.ServletConnection
    public String getProtocol() {
        return this.protocol;
    }

    @Override // jakarta.servlet.ServletConnection
    public String getProtocolConnectionId() {
        return this.protocolConnectionId;
    }

    @Override // jakarta.servlet.ServletConnection
    public boolean isSecure() {
        return this.secure;
    }
}
