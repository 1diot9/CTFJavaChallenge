package org.apache.coyote.http2;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/ConnectionSettingsRemote.class */
class ConnectionSettingsRemote extends ConnectionSettingsBase<ConnectionException> {
    private static final String ENDPOINT_NAME = "Remote(server->client)";

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConnectionSettingsRemote(String connectionId) {
        super(connectionId);
    }

    @Override // org.apache.coyote.http2.ConnectionSettingsBase
    final void throwException(String msg, Http2Error error) throws ConnectionException {
        throw new ConnectionException(msg, error);
    }

    @Override // org.apache.coyote.http2.ConnectionSettingsBase
    final String getEndpointName() {
        return ENDPOINT_NAME;
    }
}
