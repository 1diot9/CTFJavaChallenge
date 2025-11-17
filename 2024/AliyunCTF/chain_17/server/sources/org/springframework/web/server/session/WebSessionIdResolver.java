package org.springframework.web.server.session;

import java.util.List;
import org.springframework.web.server.ServerWebExchange;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/session/WebSessionIdResolver.class */
public interface WebSessionIdResolver {
    List<String> resolveSessionIds(ServerWebExchange exchange);

    void setSessionId(ServerWebExchange exchange, String sessionId);

    void expireSession(ServerWebExchange exchange);
}
