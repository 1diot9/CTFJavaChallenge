package org.springframework.web.server;

import java.security.Principal;
import java.time.Instant;
import java.util.Map;
import java.util.function.Function;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.http.codec.multipart.Part;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/ServerWebExchangeDecorator.class */
public class ServerWebExchangeDecorator implements ServerWebExchange {
    private final ServerWebExchange delegate;

    /* JADX INFO: Access modifiers changed from: protected */
    public ServerWebExchangeDecorator(ServerWebExchange delegate) {
        Assert.notNull(delegate, "ServerWebExchange 'delegate' is required.");
        this.delegate = delegate;
    }

    public ServerWebExchange getDelegate() {
        return this.delegate;
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public ServerHttpRequest getRequest() {
        return getDelegate().getRequest();
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public ServerHttpResponse getResponse() {
        return getDelegate().getResponse();
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public Map<String, Object> getAttributes() {
        return getDelegate().getAttributes();
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public Mono<WebSession> getSession() {
        return getDelegate().getSession();
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public <T extends Principal> Mono<T> getPrincipal() {
        return getDelegate().getPrincipal();
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public LocaleContext getLocaleContext() {
        return getDelegate().getLocaleContext();
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public ApplicationContext getApplicationContext() {
        return getDelegate().getApplicationContext();
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public Mono<MultiValueMap<String, String>> getFormData() {
        return getDelegate().getFormData();
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public Mono<MultiValueMap<String, Part>> getMultipartData() {
        return getDelegate().getMultipartData();
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public Mono<Void> cleanupMultipart() {
        return getDelegate().cleanupMultipart();
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public boolean isNotModified() {
        return getDelegate().isNotModified();
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public boolean checkNotModified(Instant lastModified) {
        return getDelegate().checkNotModified(lastModified);
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public boolean checkNotModified(String etag) {
        return getDelegate().checkNotModified(etag);
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public boolean checkNotModified(@Nullable String etag, Instant lastModified) {
        return getDelegate().checkNotModified(etag, lastModified);
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public String transformUrl(String url) {
        return getDelegate().transformUrl(url);
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public void addUrlTransformer(Function<String, String> transformer) {
        getDelegate().addUrlTransformer(transformer);
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public String getLogPrefix() {
        return getDelegate().getLogPrefix();
    }

    public String toString() {
        return getClass().getSimpleName() + " [delegate=" + getDelegate() + "]";
    }
}
