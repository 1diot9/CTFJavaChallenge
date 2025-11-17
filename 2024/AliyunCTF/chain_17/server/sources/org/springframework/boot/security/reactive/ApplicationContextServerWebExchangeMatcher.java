package org.springframework.boot.security.reactive;

import java.util.function.Supplier;
import org.springframework.context.ApplicationContext;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/security/reactive/ApplicationContextServerWebExchangeMatcher.class */
public abstract class ApplicationContextServerWebExchangeMatcher<C> implements ServerWebExchangeMatcher {
    private final Class<? extends C> contextClass;
    private volatile Supplier<C> context;
    private final Object contextLock = new Object();

    protected abstract Mono<ServerWebExchangeMatcher.MatchResult> matches(ServerWebExchange exchange, Supplier<C> context);

    public ApplicationContextServerWebExchangeMatcher(Class<? extends C> contextClass) {
        Assert.notNull(contextClass, "Context class must not be null");
        this.contextClass = contextClass;
    }

    public final Mono<ServerWebExchangeMatcher.MatchResult> matches(ServerWebExchange exchange) {
        if (ignoreApplicationContext(exchange.getApplicationContext())) {
            return ServerWebExchangeMatcher.MatchResult.notMatch();
        }
        return matches(exchange, getContext(exchange));
    }

    protected boolean ignoreApplicationContext(ApplicationContext applicationContext) {
        return false;
    }

    protected Supplier<C> getContext(ServerWebExchange exchange) {
        if (this.context == null) {
            synchronized (this.contextLock) {
                if (this.context == null) {
                    Supplier<C> createdContext = createContext(exchange);
                    initialized(createdContext);
                    this.context = createdContext;
                }
            }
        }
        return this.context;
    }

    protected void initialized(Supplier<C> context) {
    }

    private Supplier<C> createContext(ServerWebExchange exchange) {
        ApplicationContext context = exchange.getApplicationContext();
        Assert.state(context != null, "No ApplicationContext found on ServerWebExchange.");
        if (this.contextClass.isInstance(context)) {
            return () -> {
                return context;
            };
        }
        return () -> {
            return context.getBean(this.contextClass);
        };
    }
}
