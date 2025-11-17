package org.springframework.web.servlet.function;

import jakarta.servlet.http.Cookie;
import java.net.URI;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.function.Consumer;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/EntityResponse.class */
public interface EntityResponse<T> extends ServerResponse {

    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/EntityResponse$Builder.class */
    public interface Builder<T> {
        Builder<T> header(String headerName, String... headerValues);

        Builder<T> headers(Consumer<HttpHeaders> headersConsumer);

        Builder<T> status(HttpStatusCode status);

        Builder<T> status(int status);

        Builder<T> cookie(Cookie cookie);

        Builder<T> cookies(Consumer<MultiValueMap<String, Cookie>> cookiesConsumer);

        Builder<T> allow(HttpMethod... allowedMethods);

        Builder<T> allow(Set<HttpMethod> allowedMethods);

        Builder<T> eTag(String etag);

        Builder<T> lastModified(ZonedDateTime lastModified);

        Builder<T> lastModified(Instant lastModified);

        Builder<T> location(URI location);

        Builder<T> cacheControl(CacheControl cacheControl);

        Builder<T> varyBy(String... requestHeaders);

        Builder<T> contentLength(long contentLength);

        Builder<T> contentType(MediaType contentType);

        EntityResponse<T> build();
    }

    T entity();

    static <T> Builder<T> fromObject(T t) {
        return DefaultEntityResponseBuilder.fromObject(t);
    }

    static <T> Builder<T> fromObject(T t, ParameterizedTypeReference<T> entityType) {
        return DefaultEntityResponseBuilder.fromObject(t, entityType);
    }
}
