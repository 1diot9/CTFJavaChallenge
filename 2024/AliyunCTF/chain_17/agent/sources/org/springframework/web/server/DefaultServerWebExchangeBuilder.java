package org.springframework.web.server;

import java.security.Principal;
import java.util.function.Consumer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/DefaultServerWebExchangeBuilder.class */
public class DefaultServerWebExchangeBuilder implements ServerWebExchange.Builder {
    private final ServerWebExchange delegate;

    @Nullable
    private ServerHttpRequest request;

    @Nullable
    private ServerHttpResponse response;

    @Nullable
    private Mono<Principal> principalMono;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultServerWebExchangeBuilder(ServerWebExchange delegate) {
        Assert.notNull(delegate, "Delegate is required");
        this.delegate = delegate;
    }

    @Override // org.springframework.web.server.ServerWebExchange.Builder
    public ServerWebExchange.Builder request(Consumer<ServerHttpRequest.Builder> consumer) {
        ServerHttpRequest.Builder builder = this.delegate.getRequest().mutate();
        consumer.accept(builder);
        return request(builder.build());
    }

    @Override // org.springframework.web.server.ServerWebExchange.Builder
    public ServerWebExchange.Builder request(ServerHttpRequest request) {
        this.request = request;
        return this;
    }

    @Override // org.springframework.web.server.ServerWebExchange.Builder
    public ServerWebExchange.Builder response(ServerHttpResponse response) {
        this.response = response;
        return this;
    }

    @Override // org.springframework.web.server.ServerWebExchange.Builder
    public ServerWebExchange.Builder principal(Mono<Principal> principalMono) {
        this.principalMono = principalMono;
        return this;
    }

    @Override // org.springframework.web.server.ServerWebExchange.Builder
    public ServerWebExchange build() {
        return new MutativeDecorator(this.delegate, this.request, this.response, this.principalMono);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/DefaultServerWebExchangeBuilder$MutativeDecorator.class */
    private static class MutativeDecorator extends ServerWebExchangeDecorator {

        @Nullable
        private final ServerHttpRequest request;

        @Nullable
        private final ServerHttpResponse response;

        @Nullable
        private final Mono<Principal> principalMono;

        public MutativeDecorator(ServerWebExchange delegate, @Nullable ServerHttpRequest request, @Nullable ServerHttpResponse response, @Nullable Mono<Principal> principalMono) {
            super(delegate);
            this.request = request;
            this.response = response;
            this.principalMono = principalMono;
        }

        @Override // org.springframework.web.server.ServerWebExchangeDecorator, org.springframework.web.server.ServerWebExchange
        public ServerHttpRequest getRequest() {
            return this.request != null ? this.request : getDelegate().getRequest();
        }

        @Override // org.springframework.web.server.ServerWebExchangeDecorator, org.springframework.web.server.ServerWebExchange
        public ServerHttpResponse getResponse() {
            return this.response != null ? this.response : getDelegate().getResponse();
        }

        @Override // org.springframework.web.server.ServerWebExchangeDecorator, org.springframework.web.server.ServerWebExchange
        public <T extends Principal> Mono<T> getPrincipal() {
            return this.principalMono != null ? (Mono<T>) this.principalMono : getDelegate().getPrincipal();
        }
    }
}
