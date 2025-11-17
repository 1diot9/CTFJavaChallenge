package org.apache.tomcat.websocket;

import jakarta.websocket.HandshakeResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.tomcat.util.collections.CaseInsensitiveKeyMap;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/WsHandshakeResponse.class */
public class WsHandshakeResponse implements HandshakeResponse {
    private final Map<String, List<String>> headers = new CaseInsensitiveKeyMap();

    public WsHandshakeResponse() {
    }

    public WsHandshakeResponse(Map<String, List<String>> headers) {
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            if (this.headers.containsKey(entry.getKey())) {
                this.headers.get(entry.getKey()).addAll(entry.getValue());
            } else {
                List<String> values = new ArrayList<>(entry.getValue());
                this.headers.put(entry.getKey(), values);
            }
        }
    }

    @Override // jakarta.websocket.HandshakeResponse
    public Map<String, List<String>> getHeaders() {
        return this.headers;
    }
}
