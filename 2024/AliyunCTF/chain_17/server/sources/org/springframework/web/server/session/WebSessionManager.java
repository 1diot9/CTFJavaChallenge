package org.springframework.web.server.session;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/session/WebSessionManager.class */
public interface WebSessionManager {
    Mono<WebSession> getSession(ServerWebExchange exchange);
}
