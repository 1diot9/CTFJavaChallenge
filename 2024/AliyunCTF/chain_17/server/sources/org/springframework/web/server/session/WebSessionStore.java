package org.springframework.web.server.session;

import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/session/WebSessionStore.class */
public interface WebSessionStore {
    Mono<WebSession> createWebSession();

    Mono<WebSession> retrieveSession(String sessionId);

    Mono<Void> removeSession(String sessionId);

    Mono<WebSession> updateLastAccessTime(WebSession webSession);
}
