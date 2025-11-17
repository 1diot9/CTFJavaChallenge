package org.springframework.web.servlet.function;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.web.ErrorResponse;
import org.springframework.web.servlet.ModelAndView;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/ServerResponse.class */
public interface ServerResponse {

    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/ServerResponse$BodyBuilder.class */
    public interface BodyBuilder extends HeadersBuilder<BodyBuilder> {
        BodyBuilder contentLength(long contentLength);

        BodyBuilder contentType(MediaType contentType);

        ServerResponse body(Object body);

        <T> ServerResponse body(T body, ParameterizedTypeReference<T> bodyType);

        ServerResponse render(String name, Object... modelAttributes);

        ServerResponse render(String name, Map<String, ?> model);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/ServerResponse$Context.class */
    public interface Context {
        List<HttpMessageConverter<?>> messageConverters();
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/ServerResponse$HeadersBuilder.class */
    public interface HeadersBuilder<B extends HeadersBuilder<B>> {

        @FunctionalInterface
        /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/ServerResponse$HeadersBuilder$WriteFunction.class */
        public interface WriteFunction {
            @Nullable
            ModelAndView write(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws Exception;
        }

        B header(String headerName, String... headerValues);

        B headers(Consumer<HttpHeaders> headersConsumer);

        B cookie(Cookie cookie);

        B cookies(Consumer<MultiValueMap<String, Cookie>> cookiesConsumer);

        B allow(HttpMethod... allowedMethods);

        B allow(Set<HttpMethod> allowedMethods);

        B eTag(String eTag);

        B lastModified(ZonedDateTime lastModified);

        B lastModified(Instant lastModified);

        B location(URI location);

        B cacheControl(CacheControl cacheControl);

        B varyBy(String... requestHeaders);

        ServerResponse build();

        ServerResponse build(WriteFunction writeFunction);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/ServerResponse$SseBuilder.class */
    public interface SseBuilder {
        void send(Object object) throws IOException;

        SseBuilder id(String id);

        SseBuilder event(String eventName);

        SseBuilder retry(Duration duration);

        SseBuilder comment(String comment);

        void data(Object object) throws IOException;

        void error(Throwable t);

        void complete();

        SseBuilder onTimeout(Runnable onTimeout);

        SseBuilder onError(Consumer<Throwable> onError);

        SseBuilder onComplete(Runnable onCompletion);
    }

    HttpStatusCode statusCode();

    @Deprecated(since = "6.0")
    int rawStatusCode();

    HttpHeaders headers();

    MultiValueMap<String, Cookie> cookies();

    @Nullable
    ModelAndView writeTo(HttpServletRequest request, HttpServletResponse response, Context context) throws ServletException, IOException;

    static BodyBuilder from(ServerResponse other) {
        return new DefaultServerResponseBuilder(other);
    }

    static ServerResponse from(ErrorResponse response) {
        return status(response.getStatusCode()).headers(headers -> {
            headers.putAll(response.getHeaders());
        }).body(response.getBody());
    }

    static BodyBuilder status(HttpStatusCode status) {
        return new DefaultServerResponseBuilder(status);
    }

    static BodyBuilder status(int status) {
        return new DefaultServerResponseBuilder(HttpStatusCode.valueOf(status));
    }

    static BodyBuilder ok() {
        return status(HttpStatus.OK);
    }

    static BodyBuilder created(URI location) {
        BodyBuilder builder = status(HttpStatus.CREATED);
        return builder.location(location);
    }

    static BodyBuilder accepted() {
        return status(HttpStatus.ACCEPTED);
    }

    static HeadersBuilder<?> noContent() {
        return status(HttpStatus.NO_CONTENT);
    }

    static BodyBuilder seeOther(URI location) {
        BodyBuilder builder = status(HttpStatus.SEE_OTHER);
        return builder.location(location);
    }

    static BodyBuilder temporaryRedirect(URI location) {
        BodyBuilder builder = status(HttpStatus.TEMPORARY_REDIRECT);
        return builder.location(location);
    }

    static BodyBuilder permanentRedirect(URI location) {
        BodyBuilder builder = status(HttpStatus.PERMANENT_REDIRECT);
        return builder.location(location);
    }

    static BodyBuilder badRequest() {
        return status(HttpStatus.BAD_REQUEST);
    }

    static HeadersBuilder<?> notFound() {
        return status(HttpStatus.NOT_FOUND);
    }

    static BodyBuilder unprocessableEntity() {
        return status(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    static ServerResponse async(Object asyncResponse) {
        return DefaultAsyncServerResponse.create(asyncResponse, null);
    }

    static ServerResponse async(Object asyncResponse, Duration timeout) {
        return DefaultAsyncServerResponse.create(asyncResponse, timeout);
    }

    static ServerResponse sse(Consumer<SseBuilder> consumer) {
        return SseServerResponse.create(consumer, null);
    }

    static ServerResponse sse(Consumer<SseBuilder> consumer, Duration timeout) {
        return SseServerResponse.create(consumer, timeout);
    }
}
