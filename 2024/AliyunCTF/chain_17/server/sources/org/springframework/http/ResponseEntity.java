package org.springframework.http;

import java.net.URI;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.function.Consumer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/ResponseEntity.class */
public class ResponseEntity<T> extends HttpEntity<T> {
    private final HttpStatusCode status;

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/ResponseEntity$BodyBuilder.class */
    public interface BodyBuilder extends HeadersBuilder<BodyBuilder> {
        BodyBuilder contentLength(long contentLength);

        BodyBuilder contentType(MediaType contentType);

        <T> ResponseEntity<T> body(@Nullable T body);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/ResponseEntity$HeadersBuilder.class */
    public interface HeadersBuilder<B extends HeadersBuilder<B>> {
        B header(String headerName, String... headerValues);

        B headers(@Nullable HttpHeaders headers);

        B headers(Consumer<HttpHeaders> headersConsumer);

        B allow(HttpMethod... allowedMethods);

        B eTag(@Nullable String etag);

        B lastModified(ZonedDateTime lastModified);

        B lastModified(Instant lastModified);

        B lastModified(long lastModified);

        B location(URI location);

        B cacheControl(CacheControl cacheControl);

        B varyBy(String... requestHeaders);

        <T> ResponseEntity<T> build();
    }

    public ResponseEntity(HttpStatusCode status) {
        this((Object) null, (MultiValueMap<String, String>) null, status);
    }

    public ResponseEntity(@Nullable T body, HttpStatusCode status) {
        this(body, (MultiValueMap<String, String>) null, status);
    }

    public ResponseEntity(MultiValueMap<String, String> headers, HttpStatusCode status) {
        this((Object) null, headers, status);
    }

    public ResponseEntity(@Nullable T body, @Nullable MultiValueMap<String, String> headers, int rawStatus) {
        this(body, headers, HttpStatusCode.valueOf(rawStatus));
    }

    public ResponseEntity(@Nullable T body, @Nullable MultiValueMap<String, String> headers, HttpStatusCode statusCode) {
        super(body, headers);
        Assert.notNull(statusCode, "HttpStatusCode must not be null");
        this.status = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return this.status;
    }

    @Deprecated(since = "6.0")
    public int getStatusCodeValue() {
        return getStatusCode().value();
    }

    @Override // org.springframework.http.HttpEntity
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!super.equals(other)) {
            return false;
        }
        ResponseEntity<?> otherEntity = (ResponseEntity) other;
        return ObjectUtils.nullSafeEquals(this.status, otherEntity.status);
    }

    @Override // org.springframework.http.HttpEntity
    public int hashCode() {
        return (29 * super.hashCode()) + ObjectUtils.nullSafeHashCode(this.status);
    }

    @Override // org.springframework.http.HttpEntity
    public String toString() {
        StringBuilder builder = new StringBuilder("<");
        builder.append(this.status);
        HttpStatusCode httpStatusCode = this.status;
        if (httpStatusCode instanceof HttpStatus) {
            HttpStatus httpStatus = (HttpStatus) httpStatusCode;
            builder.append(' ');
            builder.append(httpStatus.getReasonPhrase());
        }
        builder.append(',');
        T body = getBody();
        HttpHeaders headers = getHeaders();
        if (body != null) {
            builder.append(body);
            builder.append(',');
        }
        builder.append(headers);
        builder.append('>');
        return builder.toString();
    }

    public static BodyBuilder status(HttpStatusCode status) {
        Assert.notNull(status, "HttpStatusCode must not be null");
        return new DefaultBuilder(status);
    }

    public static BodyBuilder status(int status) {
        return new DefaultBuilder(status);
    }

    public static BodyBuilder ok() {
        return status(HttpStatus.OK);
    }

    public static <T> ResponseEntity<T> ok(@Nullable T body) {
        return ok().body(body);
    }

    public static <T> ResponseEntity<T> of(Optional<T> body) {
        Assert.notNull(body, "Body must not be null");
        return (ResponseEntity) body.map(ResponseEntity::ok).orElseGet(() -> {
            return notFound().build();
        });
    }

    public static HeadersBuilder<?> of(final ProblemDetail body) {
        return new DefaultBuilder(body.getStatus()) { // from class: org.springframework.http.ResponseEntity.1
            @Override // org.springframework.http.ResponseEntity.DefaultBuilder, org.springframework.http.ResponseEntity.HeadersBuilder
            public <T> ResponseEntity<T> build() {
                return body(body);
            }
        };
    }

    public static <T> ResponseEntity<T> ofNullable(@Nullable T body) {
        if (body == null) {
            return notFound().build();
        }
        return ok(body);
    }

    public static BodyBuilder created(URI location) {
        return status(HttpStatus.CREATED).location(location);
    }

    public static BodyBuilder accepted() {
        return status(HttpStatus.ACCEPTED);
    }

    public static HeadersBuilder<?> noContent() {
        return status(HttpStatus.NO_CONTENT);
    }

    public static BodyBuilder badRequest() {
        return status(HttpStatus.BAD_REQUEST);
    }

    public static HeadersBuilder<?> notFound() {
        return status(HttpStatus.NOT_FOUND);
    }

    public static BodyBuilder unprocessableEntity() {
        return status(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    public static BodyBuilder internalServerError() {
        return status(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/ResponseEntity$DefaultBuilder.class */
    public static class DefaultBuilder implements BodyBuilder {
        private final HttpStatusCode statusCode;
        private final HttpHeaders headers;

        @Override // org.springframework.http.ResponseEntity.HeadersBuilder
        public /* bridge */ /* synthetic */ BodyBuilder headers(Consumer headersConsumer) {
            return headers((Consumer<HttpHeaders>) headersConsumer);
        }

        public DefaultBuilder(int statusCode) {
            this(HttpStatusCode.valueOf(statusCode));
        }

        public DefaultBuilder(HttpStatusCode statusCode) {
            this.headers = new HttpHeaders();
            this.statusCode = statusCode;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.http.ResponseEntity.HeadersBuilder
        public BodyBuilder header(String headerName, String... headerValues) {
            for (String headerValue : headerValues) {
                this.headers.add(headerName, headerValue);
            }
            return this;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.http.ResponseEntity.HeadersBuilder
        public BodyBuilder headers(@Nullable HttpHeaders headers) {
            if (headers != null) {
                this.headers.putAll(headers);
            }
            return this;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.http.ResponseEntity.HeadersBuilder
        public BodyBuilder headers(Consumer<HttpHeaders> headersConsumer) {
            headersConsumer.accept(this.headers);
            return this;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.http.ResponseEntity.HeadersBuilder
        public BodyBuilder allow(HttpMethod... allowedMethods) {
            this.headers.setAllow(new LinkedHashSet(Arrays.asList(allowedMethods)));
            return this;
        }

        @Override // org.springframework.http.ResponseEntity.BodyBuilder
        public BodyBuilder contentLength(long contentLength) {
            this.headers.setContentLength(contentLength);
            return this;
        }

        @Override // org.springframework.http.ResponseEntity.BodyBuilder
        public BodyBuilder contentType(MediaType contentType) {
            this.headers.setContentType(contentType);
            return this;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.http.ResponseEntity.HeadersBuilder
        public BodyBuilder eTag(@Nullable String etag) {
            if (etag != null) {
                if (!etag.startsWith("\"") && !etag.startsWith("W/\"")) {
                    etag = "\"" + etag;
                }
                if (!etag.endsWith("\"")) {
                    etag = etag + "\"";
                }
            }
            this.headers.setETag(etag);
            return this;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.http.ResponseEntity.HeadersBuilder
        public BodyBuilder lastModified(ZonedDateTime date) {
            this.headers.setLastModified(date);
            return this;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.http.ResponseEntity.HeadersBuilder
        public BodyBuilder lastModified(Instant date) {
            this.headers.setLastModified(date);
            return this;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.http.ResponseEntity.HeadersBuilder
        public BodyBuilder lastModified(long date) {
            this.headers.setLastModified(date);
            return this;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.http.ResponseEntity.HeadersBuilder
        public BodyBuilder location(URI location) {
            this.headers.setLocation(location);
            return this;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.http.ResponseEntity.HeadersBuilder
        public BodyBuilder cacheControl(CacheControl cacheControl) {
            this.headers.setCacheControl(cacheControl);
            return this;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.http.ResponseEntity.HeadersBuilder
        public BodyBuilder varyBy(String... requestHeaders) {
            this.headers.setVary(Arrays.asList(requestHeaders));
            return this;
        }

        @Override // org.springframework.http.ResponseEntity.HeadersBuilder
        public <T> ResponseEntity<T> build() {
            return body(null);
        }

        @Override // org.springframework.http.ResponseEntity.BodyBuilder
        public <T> ResponseEntity<T> body(@Nullable T body) {
            return new ResponseEntity<>(body, this.headers, this.statusCode);
        }
    }
}
