package org.springframework.web.server.session;

import java.util.List;
import java.util.Objects;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/session/DefaultWebSessionManager.class */
public class DefaultWebSessionManager implements WebSessionManager {
    private static final Log logger = LogFactory.getLog((Class<?>) DefaultWebSessionManager.class);
    private WebSessionIdResolver sessionIdResolver = new CookieWebSessionIdResolver();
    private WebSessionStore sessionStore = new InMemoryWebSessionStore();

    public void setSessionIdResolver(WebSessionIdResolver sessionIdResolver) {
        Assert.notNull(sessionIdResolver, "WebSessionIdResolver is required");
        this.sessionIdResolver = sessionIdResolver;
    }

    public WebSessionIdResolver getSessionIdResolver() {
        return this.sessionIdResolver;
    }

    public void setSessionStore(WebSessionStore sessionStore) {
        Assert.notNull(sessionStore, "WebSessionStore is required");
        this.sessionStore = sessionStore;
    }

    public WebSessionStore getSessionStore() {
        return this.sessionStore;
    }

    @Override // org.springframework.web.server.session.WebSessionManager
    public Mono<WebSession> getSession(ServerWebExchange exchange) {
        return Mono.defer(() -> {
            return retrieveSession(exchange).switchIfEmpty(createWebSession()).doOnNext(session -> {
                exchange.getResponse().beforeCommit(() -> {
                    return save(exchange, session);
                });
            });
        });
    }

    private Mono<WebSession> createWebSession() {
        Mono<WebSession> session = this.sessionStore.createWebSession();
        if (logger.isDebugEnabled()) {
            session = session.doOnNext(s -> {
                logger.debug("Created new WebSession.");
            });
        }
        return session;
    }

    private Mono<WebSession> retrieveSession(ServerWebExchange exchange) {
        Flux fromIterable = Flux.fromIterable(getSessionIdResolver().resolveSessionIds(exchange));
        WebSessionStore webSessionStore = this.sessionStore;
        Objects.requireNonNull(webSessionStore);
        return fromIterable.concatMap(webSessionStore::retrieveSession).next();
    }

    private Mono<Void> save(ServerWebExchange exchange, WebSession session) {
        List<String> ids = getSessionIdResolver().resolveSessionIds(exchange);
        if (!session.isStarted() || session.isExpired()) {
            if (!ids.isEmpty()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("WebSession expired or has been invalidated");
                }
                this.sessionIdResolver.expireSession(exchange);
            }
            return Mono.empty();
        }
        if (ids.isEmpty() || !session.getId().equals(ids.get(0))) {
            this.sessionIdResolver.setSessionId(exchange, session.getId());
        }
        return session.save();
    }
}
