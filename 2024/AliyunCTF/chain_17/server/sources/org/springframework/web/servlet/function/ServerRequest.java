package org.springframework.web.servlet.function;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Consumer;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRange;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.RequestPath;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.util.ServletRequestPathUtils;
import org.springframework.web.util.UriBuilder;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/ServerRequest.class */
public interface ServerRequest {

    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/ServerRequest$Builder.class */
    public interface Builder {
        Builder method(HttpMethod method);

        Builder uri(URI uri);

        Builder header(String headerName, String... headerValues);

        Builder headers(Consumer<HttpHeaders> headersConsumer);

        Builder cookie(String name, String... values);

        Builder cookies(Consumer<MultiValueMap<String, Cookie>> cookiesConsumer);

        Builder body(byte[] body);

        Builder body(String body);

        Builder attribute(String name, Object value);

        Builder attributes(Consumer<Map<String, Object>> attributesConsumer);

        Builder param(String name, String... values);

        Builder params(Consumer<MultiValueMap<String, String>> paramsConsumer);

        Builder remoteAddress(InetSocketAddress remoteAddress);

        ServerRequest build();
    }

    HttpMethod method();

    @Deprecated
    String methodName();

    URI uri();

    UriBuilder uriBuilder();

    Headers headers();

    MultiValueMap<String, Cookie> cookies();

    Optional<InetSocketAddress> remoteAddress();

    List<HttpMessageConverter<?>> messageConverters();

    <T> T body(Class<T> bodyType) throws ServletException, IOException;

    <T> T body(ParameterizedTypeReference<T> bodyType) throws ServletException, IOException;

    <T> T bind(Class<T> bindType, Consumer<WebDataBinder> dataBinderCustomizer) throws BindException;

    Map<String, Object> attributes();

    MultiValueMap<String, String> params();

    MultiValueMap<String, Part> multipartData() throws IOException, ServletException;

    Map<String, String> pathVariables();

    HttpSession session();

    Optional<Principal> principal();

    HttpServletRequest servletRequest();

    default String path() {
        return requestPath().pathWithinApplication().value();
    }

    @Deprecated
    default PathContainer pathContainer() {
        return requestPath();
    }

    default RequestPath requestPath() {
        return ServletRequestPathUtils.getParsedRequestPath(servletRequest());
    }

    default <T> T bind(Class<T> cls) throws BindException {
        return (T) bind(cls, dataBinder -> {
        });
    }

    default Optional<Object> attribute(String name) {
        Map<String, Object> attributes = attributes();
        if (attributes.containsKey(name)) {
            return Optional.of(attributes.get(name));
        }
        return Optional.empty();
    }

    default Optional<String> param(String name) {
        List<String> paramValues = (List) params().get(name);
        if (CollectionUtils.isEmpty(paramValues)) {
            return Optional.empty();
        }
        String value = paramValues.get(0);
        if (value == null) {
            value = "";
        }
        return Optional.of(value);
    }

    default String pathVariable(String name) {
        Map<String, String> pathVariables = pathVariables();
        if (pathVariables.containsKey(name)) {
            return pathVariables().get(name);
        }
        throw new IllegalArgumentException("No path variable with name \"" + name + "\" available");
    }

    default Optional<ServerResponse> checkNotModified(Instant lastModified) {
        Assert.notNull(lastModified, "LastModified must not be null");
        return DefaultServerRequest.checkNotModified(servletRequest(), lastModified, null);
    }

    default Optional<ServerResponse> checkNotModified(String etag) {
        Assert.notNull(etag, "Etag must not be null");
        return DefaultServerRequest.checkNotModified(servletRequest(), null, etag);
    }

    default Optional<ServerResponse> checkNotModified(Instant lastModified, String etag) {
        Assert.notNull(lastModified, "LastModified must not be null");
        Assert.notNull(etag, "Etag must not be null");
        return DefaultServerRequest.checkNotModified(servletRequest(), lastModified, etag);
    }

    static ServerRequest create(HttpServletRequest servletRequest, List<HttpMessageConverter<?>> messageReaders) {
        return new DefaultServerRequest(servletRequest, messageReaders);
    }

    static Builder from(ServerRequest other) {
        return new DefaultServerRequestBuilder(other);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/ServerRequest$Headers.class */
    public interface Headers {
        List<MediaType> accept();

        List<Charset> acceptCharset();

        List<Locale.LanguageRange> acceptLanguage();

        OptionalLong contentLength();

        Optional<MediaType> contentType();

        @Nullable
        InetSocketAddress host();

        List<HttpRange> range();

        List<String> header(String headerName);

        HttpHeaders asHttpHeaders();

        @Nullable
        default String firstHeader(String headerName) {
            List<String> list = header(headerName);
            if (list.isEmpty()) {
                return null;
            }
            return list.get(0);
        }
    }
}
