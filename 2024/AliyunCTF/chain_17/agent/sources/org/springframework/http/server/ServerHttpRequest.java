package org.springframework.http.server;

import java.net.InetSocketAddress;
import java.security.Principal;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpRequest;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/ServerHttpRequest.class */
public interface ServerHttpRequest extends HttpRequest, HttpInputMessage {
    @Nullable
    Principal getPrincipal();

    InetSocketAddress getLocalAddress();

    InetSocketAddress getRemoteAddress();

    ServerHttpAsyncRequestControl getAsyncRequestControl(ServerHttpResponse response);
}
