package org.springframework.web.servlet.function;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URI;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.function.ServerResponse;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/DefaultServerResponseBuilder.class */
class DefaultServerResponseBuilder implements ServerResponse.BodyBuilder {
    private final HttpStatusCode statusCode;
    private final HttpHeaders headers = new HttpHeaders();
    private final MultiValueMap<String, Cookie> cookies = new LinkedMultiValueMap();

    @Override // org.springframework.web.servlet.function.ServerResponse.HeadersBuilder
    public /* bridge */ /* synthetic */ ServerResponse.BodyBuilder allow(Set allowedMethods) {
        return allow((Set<HttpMethod>) allowedMethods);
    }

    @Override // org.springframework.web.servlet.function.ServerResponse.HeadersBuilder
    public /* bridge */ /* synthetic */ ServerResponse.BodyBuilder cookies(Consumer cookiesConsumer) {
        return cookies((Consumer<MultiValueMap<String, Cookie>>) cookiesConsumer);
    }

    @Override // org.springframework.web.servlet.function.ServerResponse.HeadersBuilder
    public /* bridge */ /* synthetic */ ServerResponse.BodyBuilder headers(Consumer headersConsumer) {
        return headers((Consumer<HttpHeaders>) headersConsumer);
    }

    public DefaultServerResponseBuilder(ServerResponse other) {
        Assert.notNull(other, "ServerResponse must not be null");
        this.statusCode = other.statusCode();
        this.headers.addAll(other.headers());
        this.cookies.addAll(other.cookies());
    }

    public DefaultServerResponseBuilder(HttpStatusCode status) {
        Assert.notNull(status, "HttpStatusCode must not be null");
        this.statusCode = status;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.web.servlet.function.ServerResponse.HeadersBuilder
    public ServerResponse.BodyBuilder header(String headerName, String... headerValues) {
        Assert.notNull(headerName, "HeaderName must not be null");
        for (String headerValue : headerValues) {
            this.headers.add(headerName, headerValue);
        }
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.web.servlet.function.ServerResponse.HeadersBuilder
    public ServerResponse.BodyBuilder headers(Consumer<HttpHeaders> headersConsumer) {
        Assert.notNull(headersConsumer, "HeaderConsumer must not be null");
        headersConsumer.accept(this.headers);
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.web.servlet.function.ServerResponse.HeadersBuilder
    public ServerResponse.BodyBuilder cookie(Cookie cookie) {
        Assert.notNull(cookie, "Cookie must not be null");
        this.cookies.add(cookie.getName(), cookie);
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.web.servlet.function.ServerResponse.HeadersBuilder
    public ServerResponse.BodyBuilder cookies(Consumer<MultiValueMap<String, Cookie>> cookiesConsumer) {
        Assert.notNull(cookiesConsumer, "CookiesConsumer must not be null");
        cookiesConsumer.accept(this.cookies);
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.web.servlet.function.ServerResponse.HeadersBuilder
    public ServerResponse.BodyBuilder allow(HttpMethod... allowedMethods) {
        Assert.notNull(allowedMethods, "Http AllowedMethods must not be null");
        this.headers.setAllow(new LinkedHashSet(Arrays.asList(allowedMethods)));
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.web.servlet.function.ServerResponse.HeadersBuilder
    public ServerResponse.BodyBuilder allow(Set<HttpMethod> allowedMethods) {
        Assert.notNull(allowedMethods, "Http AllowedMethods must not be null");
        this.headers.setAllow(allowedMethods);
        return this;
    }

    @Override // org.springframework.web.servlet.function.ServerResponse.BodyBuilder
    public ServerResponse.BodyBuilder contentLength(long contentLength) {
        this.headers.setContentLength(contentLength);
        return this;
    }

    @Override // org.springframework.web.servlet.function.ServerResponse.BodyBuilder
    public ServerResponse.BodyBuilder contentType(MediaType contentType) {
        Assert.notNull(contentType, "ContentType must not be null");
        this.headers.setContentType(contentType);
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.web.servlet.function.ServerResponse.HeadersBuilder
    public ServerResponse.BodyBuilder eTag(String etag) {
        Assert.notNull(etag, "etag must not be null");
        if (!etag.startsWith("\"") && !etag.startsWith("W/\"")) {
            etag = "\"" + etag;
        }
        if (!etag.endsWith("\"")) {
            etag = etag + "\"";
        }
        this.headers.setETag(etag);
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.web.servlet.function.ServerResponse.HeadersBuilder
    public ServerResponse.BodyBuilder lastModified(ZonedDateTime lastModified) {
        this.headers.setLastModified(lastModified);
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.web.servlet.function.ServerResponse.HeadersBuilder
    public ServerResponse.BodyBuilder lastModified(Instant lastModified) {
        this.headers.setLastModified(lastModified);
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.web.servlet.function.ServerResponse.HeadersBuilder
    public ServerResponse.BodyBuilder location(URI location) {
        this.headers.setLocation(location);
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.web.servlet.function.ServerResponse.HeadersBuilder
    public ServerResponse.BodyBuilder cacheControl(CacheControl cacheControl) {
        this.headers.setCacheControl(cacheControl);
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.web.servlet.function.ServerResponse.HeadersBuilder
    public ServerResponse.BodyBuilder varyBy(String... requestHeaders) {
        this.headers.setVary(Arrays.asList(requestHeaders));
        return this;
    }

    @Override // org.springframework.web.servlet.function.ServerResponse.HeadersBuilder
    public ServerResponse build() {
        return build((request, response) -> {
            return null;
        });
    }

    @Override // org.springframework.web.servlet.function.ServerResponse.HeadersBuilder
    public ServerResponse build(ServerResponse.HeadersBuilder.WriteFunction writeFunction) {
        return new WriteFunctionResponse(this.statusCode, this.headers, this.cookies, writeFunction);
    }

    @Override // org.springframework.web.servlet.function.ServerResponse.BodyBuilder
    public ServerResponse body(Object body) {
        return DefaultEntityResponseBuilder.fromObject(body).status(this.statusCode).headers(headers -> {
            headers.putAll(this.headers);
        }).cookies(cookies -> {
            cookies.addAll(this.cookies);
        }).build();
    }

    @Override // org.springframework.web.servlet.function.ServerResponse.BodyBuilder
    public <T> ServerResponse body(T body, ParameterizedTypeReference<T> bodyType) {
        return DefaultEntityResponseBuilder.fromObject(body, bodyType).status(this.statusCode).headers(headers -> {
            headers.putAll(this.headers);
        }).cookies(cookies -> {
            cookies.addAll(this.cookies);
        }).build();
    }

    @Override // org.springframework.web.servlet.function.ServerResponse.BodyBuilder
    public ServerResponse render(String name, Object... modelAttributes) {
        return new DefaultRenderingResponseBuilder(name).status(this.statusCode).headers(headers -> {
            headers.putAll(this.headers);
        }).cookies(cookies -> {
            cookies.addAll(this.cookies);
        }).modelAttributes(modelAttributes).build();
    }

    @Override // org.springframework.web.servlet.function.ServerResponse.BodyBuilder
    public ServerResponse render(String name, Map<String, ?> model) {
        return new DefaultRenderingResponseBuilder(name).status(this.statusCode).headers(headers -> {
            headers.putAll(this.headers);
        }).cookies(cookies -> {
            cookies.addAll(this.cookies);
        }).modelAttributes(model).build();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/DefaultServerResponseBuilder$WriteFunctionResponse.class */
    public static class WriteFunctionResponse extends AbstractServerResponse {
        private final ServerResponse.HeadersBuilder.WriteFunction writeFunction;

        public WriteFunctionResponse(HttpStatusCode statusCode, HttpHeaders headers, MultiValueMap<String, Cookie> cookies, ServerResponse.HeadersBuilder.WriteFunction writeFunction) {
            super(statusCode, headers, cookies);
            Assert.notNull(writeFunction, "WriteFunction must not be null");
            this.writeFunction = writeFunction;
        }

        @Override // org.springframework.web.servlet.function.AbstractServerResponse
        protected ModelAndView writeToInternal(HttpServletRequest request, HttpServletResponse response, ServerResponse.Context context) throws Exception {
            return this.writeFunction.write(request, response);
        }
    }
}
