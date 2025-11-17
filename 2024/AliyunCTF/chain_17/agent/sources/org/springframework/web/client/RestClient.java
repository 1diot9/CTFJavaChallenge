package org.springframework.web.client;

import io.micrometer.observation.ObservationRegistry;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInitializer;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.observation.ClientRequestObservationConvention;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriBuilderFactory;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/RestClient.class */
public interface RestClient {

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/RestClient$Builder.class */
    public interface Builder {
        Builder baseUrl(String baseUrl);

        Builder defaultUriVariables(Map<String, ?> defaultUriVariables);

        Builder uriBuilderFactory(UriBuilderFactory uriBuilderFactory);

        Builder defaultHeader(String header, String... values);

        Builder defaultHeaders(Consumer<HttpHeaders> headersConsumer);

        Builder defaultRequest(Consumer<RequestHeadersSpec<?>> defaultRequest);

        Builder defaultStatusHandler(Predicate<HttpStatusCode> statusPredicate, ResponseSpec.ErrorHandler errorHandler);

        Builder defaultStatusHandler(ResponseErrorHandler errorHandler);

        Builder requestInterceptor(ClientHttpRequestInterceptor interceptor);

        Builder requestInterceptors(Consumer<List<ClientHttpRequestInterceptor>> interceptorsConsumer);

        Builder requestInitializer(ClientHttpRequestInitializer initializer);

        Builder requestInitializers(Consumer<List<ClientHttpRequestInitializer>> initializersConsumer);

        Builder requestFactory(ClientHttpRequestFactory requestFactory);

        Builder messageConverters(Consumer<List<HttpMessageConverter<?>>> configurer);

        Builder observationRegistry(ObservationRegistry observationRegistry);

        Builder observationConvention(ClientRequestObservationConvention observationConvention);

        Builder apply(Consumer<Builder> builderConsumer);

        /* renamed from: clone */
        Builder m2685clone();

        RestClient build();
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/RestClient$RequestBodySpec.class */
    public interface RequestBodySpec extends RequestHeadersSpec<RequestBodySpec> {
        RequestBodySpec contentLength(long contentLength);

        RequestBodySpec contentType(MediaType contentType);

        RequestBodySpec body(Object body);

        <T> RequestBodySpec body(T body, ParameterizedTypeReference<T> bodyType);

        RequestBodySpec body(StreamingHttpOutputMessage.Body body);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/RestClient$RequestBodyUriSpec.class */
    public interface RequestBodyUriSpec extends RequestBodySpec, RequestHeadersUriSpec<RequestBodySpec> {
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/RestClient$RequestHeadersUriSpec.class */
    public interface RequestHeadersUriSpec<S extends RequestHeadersSpec<S>> extends UriSpec<S>, RequestHeadersSpec<S> {
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/RestClient$ResponseSpec.class */
    public interface ResponseSpec {

        @FunctionalInterface
        /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/RestClient$ResponseSpec$ErrorHandler.class */
        public interface ErrorHandler {
            void handle(HttpRequest request, ClientHttpResponse response) throws IOException;
        }

        ResponseSpec onStatus(Predicate<HttpStatusCode> statusPredicate, ErrorHandler errorHandler);

        ResponseSpec onStatus(ResponseErrorHandler errorHandler);

        @Nullable
        <T> T body(Class<T> bodyType);

        @Nullable
        <T> T body(ParameterizedTypeReference<T> bodyType);

        <T> ResponseEntity<T> toEntity(Class<T> bodyType);

        <T> ResponseEntity<T> toEntity(ParameterizedTypeReference<T> bodyType);

        ResponseEntity<Void> toBodilessEntity();
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/RestClient$UriSpec.class */
    public interface UriSpec<S extends RequestHeadersSpec<?>> {
        S uri(URI uri);

        S uri(String uri, Object... uriVariables);

        S uri(String uri, Map<String, ?> uriVariables);

        S uri(String uri, Function<UriBuilder, URI> uriFunction);

        S uri(Function<UriBuilder, URI> uriFunction);
    }

    RequestHeadersUriSpec<?> get();

    RequestHeadersUriSpec<?> head();

    RequestBodyUriSpec post();

    RequestBodyUriSpec put();

    RequestBodyUriSpec patch();

    RequestHeadersUriSpec<?> delete();

    RequestHeadersUriSpec<?> options();

    RequestBodyUriSpec method(HttpMethod method);

    Builder mutate();

    static RestClient create() {
        return new DefaultRestClientBuilder().build();
    }

    static RestClient create(String baseUrl) {
        return new DefaultRestClientBuilder().baseUrl(baseUrl).build();
    }

    static RestClient create(RestTemplate restTemplate) {
        return new DefaultRestClientBuilder(restTemplate).build();
    }

    static Builder builder() {
        return new DefaultRestClientBuilder();
    }

    static Builder builder(RestTemplate restTemplate) {
        return new DefaultRestClientBuilder(restTemplate);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/RestClient$RequestHeadersSpec.class */
    public interface RequestHeadersSpec<S extends RequestHeadersSpec<S>> {

        /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/RestClient$RequestHeadersSpec$ConvertibleClientHttpResponse.class */
        public interface ConvertibleClientHttpResponse extends ClientHttpResponse {
            @Nullable
            <T> T bodyTo(Class<T> bodyType);

            @Nullable
            <T> T bodyTo(ParameterizedTypeReference<T> bodyType);
        }

        @FunctionalInterface
        /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/RestClient$RequestHeadersSpec$ExchangeFunction.class */
        public interface ExchangeFunction<T> {
            T exchange(HttpRequest clientRequest, ConvertibleClientHttpResponse clientResponse) throws IOException;
        }

        S accept(MediaType... acceptableMediaTypes);

        S acceptCharset(Charset... acceptableCharsets);

        S ifModifiedSince(ZonedDateTime ifModifiedSince);

        S ifNoneMatch(String... ifNoneMatches);

        S header(String headerName, String... headerValues);

        S headers(Consumer<HttpHeaders> headersConsumer);

        S httpRequest(Consumer<ClientHttpRequest> requestConsumer);

        ResponseSpec retrieve();

        <T> T exchange(ExchangeFunction<T> exchangeFunction, boolean close);

        default <T> T exchange(ExchangeFunction<T> exchangeFunction) {
            return (T) exchange(exchangeFunction, true);
        }
    }
}
