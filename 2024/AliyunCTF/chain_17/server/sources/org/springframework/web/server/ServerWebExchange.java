package org.springframework.web.server;

import java.security.Principal;
import java.time.Instant;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.http.codec.multipart.Part;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/ServerWebExchange.class */
public interface ServerWebExchange {
    public static final String LOG_ID_ATTRIBUTE = ServerWebExchange.class.getName() + ".LOG_ID";

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/ServerWebExchange$Builder.class */
    public interface Builder {
        Builder request(Consumer<ServerHttpRequest.Builder> requestBuilderConsumer);

        Builder request(ServerHttpRequest request);

        Builder response(ServerHttpResponse response);

        Builder principal(Mono<Principal> principalMono);

        ServerWebExchange build();
    }

    ServerHttpRequest getRequest();

    ServerHttpResponse getResponse();

    Map<String, Object> getAttributes();

    Mono<WebSession> getSession();

    <T extends Principal> Mono<T> getPrincipal();

    Mono<MultiValueMap<String, String>> getFormData();

    Mono<MultiValueMap<String, Part>> getMultipartData();

    LocaleContext getLocaleContext();

    @Nullable
    ApplicationContext getApplicationContext();

    boolean isNotModified();

    boolean checkNotModified(Instant lastModified);

    boolean checkNotModified(String etag);

    boolean checkNotModified(@Nullable String etag, Instant lastModified);

    String transformUrl(String url);

    void addUrlTransformer(Function<String, String> transformer);

    String getLogPrefix();

    @Nullable
    default <T> T getAttribute(String str) {
        return (T) getAttributes().get(str);
    }

    default <T> T getRequiredAttribute(String str) {
        T t = (T) getAttribute(str);
        Assert.notNull(t, (Supplier<String>) () -> {
            return "Required attribute '" + str + "' is missing";
        });
        return t;
    }

    default <T> T getAttributeOrDefault(String str, T t) {
        return (T) getAttributes().getOrDefault(str, t);
    }

    default Mono<Void> cleanupMultipart() {
        return getMultipartData().onErrorComplete().flatMapIterable((v0) -> {
            return v0.values();
        }).flatMapIterable(Function.identity()).flatMap(part -> {
            return part.delete().onErrorComplete();
        }).then();
    }

    default Builder mutate() {
        return new DefaultServerWebExchangeBuilder(this);
    }
}
