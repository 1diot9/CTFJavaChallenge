package org.springframework.web.client;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.charset.Charset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
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
import org.springframework.http.client.InterceptingClientHttpRequestFactory;
import org.springframework.http.client.observation.ClientHttpObservationDocumentation;
import org.springframework.http.client.observation.ClientRequestObservationContext;
import org.springframework.http.client.observation.ClientRequestObservationConvention;
import org.springframework.http.client.observation.DefaultClientRequestObservationConvention;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriBuilderFactory;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/DefaultRestClient.class */
public final class DefaultRestClient implements RestClient {
    private static final Log logger = LogFactory.getLog((Class<?>) DefaultRestClient.class);
    private static final ClientRequestObservationConvention DEFAULT_OBSERVATION_CONVENTION = new DefaultClientRequestObservationConvention();
    private final ClientHttpRequestFactory clientRequestFactory;

    @Nullable
    private volatile ClientHttpRequestFactory interceptingRequestFactory;

    @Nullable
    private final List<ClientHttpRequestInitializer> initializers;

    @Nullable
    private final List<ClientHttpRequestInterceptor> interceptors;
    private final UriBuilderFactory uriBuilderFactory;

    @Nullable
    private final HttpHeaders defaultHeaders;
    private final List<StatusHandler> defaultStatusHandlers;
    private final DefaultRestClientBuilder builder;
    private final List<HttpMessageConverter<?>> messageConverters;
    private final ObservationRegistry observationRegistry;

    @Nullable
    private final ClientRequestObservationConvention observationConvention;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultRestClient(ClientHttpRequestFactory clientRequestFactory, @Nullable List<ClientHttpRequestInterceptor> interceptors, @Nullable List<ClientHttpRequestInitializer> initializers, UriBuilderFactory uriBuilderFactory, @Nullable HttpHeaders defaultHeaders, @Nullable List<StatusHandler> statusHandlers, List<HttpMessageConverter<?>> messageConverters, ObservationRegistry observationRegistry, @Nullable ClientRequestObservationConvention observationConvention, DefaultRestClientBuilder builder) {
        this.clientRequestFactory = clientRequestFactory;
        this.initializers = initializers;
        this.interceptors = interceptors;
        this.uriBuilderFactory = uriBuilderFactory;
        this.defaultHeaders = defaultHeaders;
        this.defaultStatusHandlers = statusHandlers != null ? new ArrayList(statusHandlers) : new ArrayList();
        this.messageConverters = messageConverters;
        this.observationRegistry = observationRegistry;
        this.observationConvention = observationConvention;
        this.builder = builder;
    }

    @Override // org.springframework.web.client.RestClient
    public RestClient.RequestHeadersUriSpec<?> get() {
        return methodInternal(HttpMethod.GET);
    }

    @Override // org.springframework.web.client.RestClient
    public RestClient.RequestHeadersUriSpec<?> head() {
        return methodInternal(HttpMethod.HEAD);
    }

    @Override // org.springframework.web.client.RestClient
    public RestClient.RequestBodyUriSpec post() {
        return methodInternal(HttpMethod.POST);
    }

    @Override // org.springframework.web.client.RestClient
    public RestClient.RequestBodyUriSpec put() {
        return methodInternal(HttpMethod.PUT);
    }

    @Override // org.springframework.web.client.RestClient
    public RestClient.RequestBodyUriSpec patch() {
        return methodInternal(HttpMethod.PATCH);
    }

    @Override // org.springframework.web.client.RestClient
    public RestClient.RequestHeadersUriSpec<?> delete() {
        return methodInternal(HttpMethod.DELETE);
    }

    @Override // org.springframework.web.client.RestClient
    public RestClient.RequestHeadersUriSpec<?> options() {
        return methodInternal(HttpMethod.OPTIONS);
    }

    @Override // org.springframework.web.client.RestClient
    public RestClient.RequestBodyUriSpec method(HttpMethod method) {
        Assert.notNull(method, "HttpMethod must not be null");
        return methodInternal(method);
    }

    private RestClient.RequestBodyUriSpec methodInternal(HttpMethod httpMethod) {
        return new DefaultRequestBodyUriSpec(httpMethod);
    }

    @Override // org.springframework.web.client.RestClient
    public RestClient.Builder mutate() {
        return new DefaultRestClientBuilder(this.builder);
    }

    @Nullable
    private <T> T readWithMessageConverters(ClientHttpResponse clientHttpResponse, Runnable runnable, Type type, Class<T> cls) {
        Throwable th;
        MediaType contentType = getContentType(clientHttpResponse);
        try {
            try {
                runnable.run();
                IntrospectingClientHttpResponse introspectingClientHttpResponse = new IntrospectingClientHttpResponse(clientHttpResponse);
                if (introspectingClientHttpResponse.hasMessageBody() && !introspectingClientHttpResponse.hasEmptyMessageBody()) {
                    for (HttpMessageConverter<?> httpMessageConverter : this.messageConverters) {
                        if (httpMessageConverter instanceof GenericHttpMessageConverter) {
                            GenericHttpMessageConverter genericHttpMessageConverter = (GenericHttpMessageConverter) httpMessageConverter;
                            if (genericHttpMessageConverter.canRead(type, null, contentType)) {
                                if (logger.isDebugEnabled()) {
                                    logger.debug("Reading to [" + ResolvableType.forType(type) + "]");
                                }
                                T t = (T) genericHttpMessageConverter.read(type, null, introspectingClientHttpResponse);
                                if (clientHttpResponse != null) {
                                    clientHttpResponse.close();
                                }
                                return t;
                            }
                        }
                        if (httpMessageConverter.canRead(cls, contentType)) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Reading to [" + cls.getName() + "] as \"" + contentType + "\"");
                            }
                            T t2 = (T) httpMessageConverter.read2(cls, introspectingClientHttpResponse);
                            if (clientHttpResponse != null) {
                                clientHttpResponse.close();
                            }
                            return t2;
                        }
                    }
                    throw new UnknownContentTypeException(type, contentType, introspectingClientHttpResponse.getStatusCode(), introspectingClientHttpResponse.getStatusText(), introspectingClientHttpResponse.getHeaders(), RestClientUtils.getBody(introspectingClientHttpResponse));
                }
                if (clientHttpResponse != null) {
                    clientHttpResponse.close();
                }
                return null;
            } finally {
            }
        } catch (IOException | UncheckedIOException | HttpMessageNotReadableException e) {
            if (e instanceof UncheckedIOException) {
                th = ((UncheckedIOException) e).getCause();
            } else {
                th = e;
            }
            throw new RestClientException("Error while extracting response for type [" + ResolvableType.forType(type) + "] and content type [" + contentType + "]", th);
        }
    }

    private static MediaType getContentType(ClientHttpResponse clientResponse) {
        MediaType contentType = clientResponse.getHeaders().getContentType();
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM;
        }
        return contentType;
    }

    private static <T> Class<T> bodyClass(Type type) {
        if (type instanceof Class) {
            return (Class) type;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();
            return rawType instanceof Class ? (Class) rawType : Object.class;
        }
        return Object.class;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/DefaultRestClient$DefaultRequestBodyUriSpec.class */
    public class DefaultRequestBodyUriSpec implements RestClient.RequestBodyUriSpec {
        private final HttpMethod httpMethod;

        @Nullable
        private URI uri;

        @Nullable
        private HttpHeaders headers;

        @Nullable
        private InternalBody body;

        @Nullable
        private String uriTemplate;

        @Nullable
        private Consumer<ClientHttpRequest> httpRequestConsumer;

        /* JADX INFO: Access modifiers changed from: private */
        @FunctionalInterface
        /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/DefaultRestClient$DefaultRequestBodyUriSpec$InternalBody.class */
        public interface InternalBody {
            void writeTo(ClientHttpRequest request) throws IOException;
        }

        @Override // org.springframework.web.client.RestClient.RequestHeadersSpec
        public /* bridge */ /* synthetic */ RestClient.RequestBodySpec httpRequest(Consumer requestConsumer) {
            return httpRequest((Consumer<ClientHttpRequest>) requestConsumer);
        }

        @Override // org.springframework.web.client.RestClient.RequestHeadersSpec
        /* renamed from: headers, reason: avoid collision after fix types in other method */
        public /* bridge */ /* synthetic */ RestClient.RequestBodySpec headers2(Consumer headersConsumer) {
            return headers((Consumer<HttpHeaders>) headersConsumer);
        }

        @Override // org.springframework.web.client.RestClient.UriSpec
        public /* bridge */ /* synthetic */ RestClient.RequestHeadersSpec uri(Function uriFunction) {
            return uri((Function<UriBuilder, URI>) uriFunction);
        }

        @Override // org.springframework.web.client.RestClient.UriSpec
        public /* bridge */ /* synthetic */ RestClient.RequestHeadersSpec uri(String uriTemplate, Function uriFunction) {
            return uri(uriTemplate, (Function<UriBuilder, URI>) uriFunction);
        }

        @Override // org.springframework.web.client.RestClient.UriSpec
        public /* bridge */ /* synthetic */ RestClient.RequestHeadersSpec uri(String uriTemplate, Map uriVariables) {
            return uri(uriTemplate, (Map<String, ?>) uriVariables);
        }

        public DefaultRequestBodyUriSpec(HttpMethod httpMethod) {
            this.httpMethod = httpMethod;
        }

        @Override // org.springframework.web.client.RestClient.UriSpec
        public RestClient.RequestBodySpec uri(String uriTemplate, Object... uriVariables) {
            this.uriTemplate = uriTemplate;
            return uri(DefaultRestClient.this.uriBuilderFactory.expand(uriTemplate, uriVariables));
        }

        @Override // org.springframework.web.client.RestClient.UriSpec
        public RestClient.RequestBodySpec uri(String uriTemplate, Map<String, ?> uriVariables) {
            this.uriTemplate = uriTemplate;
            return uri(DefaultRestClient.this.uriBuilderFactory.expand(uriTemplate, uriVariables));
        }

        @Override // org.springframework.web.client.RestClient.UriSpec
        public RestClient.RequestBodySpec uri(String uriTemplate, Function<UriBuilder, URI> uriFunction) {
            this.uriTemplate = uriTemplate;
            return uri(uriFunction.apply(DefaultRestClient.this.uriBuilderFactory.uriString(uriTemplate)));
        }

        @Override // org.springframework.web.client.RestClient.UriSpec
        public RestClient.RequestBodySpec uri(Function<UriBuilder, URI> uriFunction) {
            return uri(uriFunction.apply(DefaultRestClient.this.uriBuilderFactory.builder()));
        }

        @Override // org.springframework.web.client.RestClient.UriSpec
        public RestClient.RequestBodySpec uri(URI uri) {
            this.uri = uri;
            return this;
        }

        private HttpHeaders getHeaders() {
            if (this.headers == null) {
                this.headers = new HttpHeaders();
            }
            return this.headers;
        }

        @Override // org.springframework.web.client.RestClient.RequestHeadersSpec
        /* renamed from: header, reason: merged with bridge method [inline-methods] */
        public RestClient.RequestBodySpec header2(String headerName, String... headerValues) {
            for (String headerValue : headerValues) {
                getHeaders().add(headerName, headerValue);
            }
            return this;
        }

        @Override // org.springframework.web.client.RestClient.RequestHeadersSpec
        public RestClient.RequestBodySpec headers(Consumer<HttpHeaders> headersConsumer) {
            headersConsumer.accept(getHeaders());
            return this;
        }

        @Override // org.springframework.web.client.RestClient.RequestHeadersSpec
        /* renamed from: accept, reason: merged with bridge method [inline-methods] */
        public RestClient.RequestBodySpec accept2(MediaType... acceptableMediaTypes) {
            getHeaders().setAccept(Arrays.asList(acceptableMediaTypes));
            return this;
        }

        @Override // org.springframework.web.client.RestClient.RequestHeadersSpec
        /* renamed from: acceptCharset, reason: merged with bridge method [inline-methods] */
        public RestClient.RequestBodySpec acceptCharset2(Charset... acceptableCharsets) {
            getHeaders().setAcceptCharset(Arrays.asList(acceptableCharsets));
            return this;
        }

        @Override // org.springframework.web.client.RestClient.RequestBodySpec
        public DefaultRequestBodyUriSpec contentType(MediaType contentType) {
            getHeaders().setContentType(contentType);
            return this;
        }

        @Override // org.springframework.web.client.RestClient.RequestBodySpec
        public DefaultRequestBodyUriSpec contentLength(long contentLength) {
            getHeaders().setContentLength(contentLength);
            return this;
        }

        @Override // org.springframework.web.client.RestClient.RequestHeadersSpec
        /* renamed from: ifModifiedSince, reason: merged with bridge method [inline-methods] */
        public RestClient.RequestBodySpec ifModifiedSince2(ZonedDateTime ifModifiedSince) {
            getHeaders().setIfModifiedSince(ifModifiedSince);
            return this;
        }

        @Override // org.springframework.web.client.RestClient.RequestHeadersSpec
        /* renamed from: ifNoneMatch, reason: merged with bridge method [inline-methods] */
        public RestClient.RequestBodySpec ifNoneMatch2(String... ifNoneMatches) {
            getHeaders().setIfNoneMatch(Arrays.asList(ifNoneMatches));
            return this;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.web.client.RestClient.RequestHeadersSpec
        public RestClient.RequestBodySpec httpRequest(Consumer<ClientHttpRequest> requestConsumer) {
            this.httpRequestConsumer = this.httpRequestConsumer != null ? this.httpRequestConsumer.andThen(requestConsumer) : requestConsumer;
            return this;
        }

        @Override // org.springframework.web.client.RestClient.RequestBodySpec
        public RestClient.RequestBodySpec body(Object body) {
            this.body = clientHttpRequest -> {
                writeWithMessageConverters(body, body.getClass(), clientHttpRequest);
            };
            return this;
        }

        @Override // org.springframework.web.client.RestClient.RequestBodySpec
        public <T> RestClient.RequestBodySpec body(T body, ParameterizedTypeReference<T> bodyType) {
            this.body = clientHttpRequest -> {
                writeWithMessageConverters(body, bodyType.getType(), clientHttpRequest);
            };
            return this;
        }

        @Override // org.springframework.web.client.RestClient.RequestBodySpec
        public RestClient.RequestBodySpec body(StreamingHttpOutputMessage.Body body) {
            this.body = request -> {
                body.writeTo(request.getBody());
            };
            return this;
        }

        private void writeWithMessageConverters(Object body, Type bodyType, ClientHttpRequest clientRequest) throws IOException {
            MediaType contentType = clientRequest.getHeaders().getContentType();
            Class<?> bodyClass = body.getClass();
            for (HttpMessageConverter messageConverter : DefaultRestClient.this.messageConverters) {
                if (messageConverter instanceof GenericHttpMessageConverter) {
                    GenericHttpMessageConverter genericMessageConverter = (GenericHttpMessageConverter) messageConverter;
                    if (genericMessageConverter.canWrite(bodyType, bodyClass, contentType)) {
                        logBody(body, contentType, genericMessageConverter);
                        genericMessageConverter.write(body, bodyType, contentType, clientRequest);
                        return;
                    }
                }
                if (messageConverter.canWrite(bodyClass, contentType)) {
                    logBody(body, contentType, messageConverter);
                    messageConverter.write(body, contentType, clientRequest);
                    return;
                }
            }
            String message = "No HttpMessageConverter for " + bodyClass.getName();
            if (contentType != null) {
                message = message + " and content type \"" + contentType + "\"";
            }
            throw new RestClientException(message);
        }

        private void logBody(Object body, @Nullable MediaType mediaType, HttpMessageConverter<?> converter) {
            if (DefaultRestClient.logger.isDebugEnabled()) {
                StringBuilder msg = new StringBuilder("Writing [");
                msg.append(body);
                msg.append("] ");
                if (mediaType != null) {
                    msg.append("as \"");
                    msg.append(mediaType);
                    msg.append("\" ");
                }
                msg.append("with ");
                msg.append(converter.getClass().getName());
                DefaultRestClient.logger.debug(msg.toString());
            }
        }

        @Override // org.springframework.web.client.RestClient.RequestHeadersSpec
        public RestClient.ResponseSpec retrieve() {
            return (RestClient.ResponseSpec) exchangeInternal((x$0, x$1) -> {
                return new DefaultResponseSpec(x$0, x$1);
            }, false);
        }

        @Override // org.springframework.web.client.RestClient.RequestHeadersSpec
        public <T> T exchange(RestClient.RequestHeadersSpec.ExchangeFunction<T> exchangeFunction, boolean z) {
            return (T) exchangeInternal(exchangeFunction, z);
        }

        private <T> T exchangeInternal(RestClient.RequestHeadersSpec.ExchangeFunction<T> exchangeFunction, boolean close) {
            Assert.notNull(exchangeFunction, "ExchangeFunction must not be null");
            ClientHttpResponse clientResponse = null;
            Observation observation = null;
            URI uri = null;
            try {
                try {
                    try {
                        uri = initUri();
                        HttpHeaders headers = initHeaders();
                        ClientHttpRequest clientRequest = createRequest(uri);
                        clientRequest.getHeaders().addAll(headers);
                        ClientRequestObservationContext observationContext = new ClientRequestObservationContext(clientRequest);
                        observationContext.setUriTemplate(this.uriTemplate);
                        observation = ClientHttpObservationDocumentation.HTTP_CLIENT_EXCHANGES.observation(DefaultRestClient.this.observationConvention, DefaultRestClient.DEFAULT_OBSERVATION_CONVENTION, () -> {
                            return observationContext;
                        }, DefaultRestClient.this.observationRegistry).start();
                        if (this.body != null) {
                            this.body.writeTo(clientRequest);
                        }
                        if (this.httpRequestConsumer != null) {
                            this.httpRequestConsumer.accept(clientRequest);
                        }
                        clientResponse = clientRequest.execute();
                        observationContext.setResponse(clientResponse);
                        RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse convertibleWrapper = new DefaultConvertibleClientHttpResponse(clientResponse);
                        T exchange = exchangeFunction.exchange(clientRequest, convertibleWrapper);
                        if (close && clientResponse != null) {
                            clientResponse.close();
                        }
                        if (observation != null) {
                            observation.stop();
                        }
                        return exchange;
                    } catch (IOException ex) {
                        ResourceAccessException resourceAccessException = createResourceAccessException(uri, this.httpMethod, ex);
                        if (observation != null) {
                            observation.error(resourceAccessException);
                        }
                        throw resourceAccessException;
                    }
                } catch (Throwable error) {
                    if (observation != null) {
                        observation.error(error);
                    }
                    throw error;
                }
            } catch (Throwable th) {
                if (close && clientResponse != null) {
                    clientResponse.close();
                }
                if (observation != null) {
                    observation.stop();
                }
                throw th;
            }
        }

        private URI initUri() {
            return this.uri != null ? this.uri : DefaultRestClient.this.uriBuilderFactory.expand("", new Object[0]);
        }

        private HttpHeaders initHeaders() {
            HttpHeaders defaultHeaders = DefaultRestClient.this.defaultHeaders;
            if (CollectionUtils.isEmpty(this.headers)) {
                return defaultHeaders != null ? defaultHeaders : new HttpHeaders();
            }
            if (CollectionUtils.isEmpty(defaultHeaders)) {
                return this.headers;
            }
            HttpHeaders result = new HttpHeaders();
            result.putAll(defaultHeaders);
            result.putAll(this.headers);
            return result;
        }

        private ClientHttpRequest createRequest(URI uri) throws IOException {
            ClientHttpRequestFactory factory;
            if (DefaultRestClient.this.interceptors != null) {
                factory = DefaultRestClient.this.interceptingRequestFactory;
                if (factory == null) {
                    factory = new InterceptingClientHttpRequestFactory(DefaultRestClient.this.clientRequestFactory, DefaultRestClient.this.interceptors);
                    DefaultRestClient.this.interceptingRequestFactory = factory;
                }
            } else {
                factory = DefaultRestClient.this.clientRequestFactory;
            }
            ClientHttpRequest request = factory.createRequest(uri, this.httpMethod);
            if (DefaultRestClient.this.initializers != null) {
                DefaultRestClient.this.initializers.forEach(initializer -> {
                    initializer.initialize(request);
                });
            }
            return request;
        }

        private static ResourceAccessException createResourceAccessException(URI url, HttpMethod method, IOException ex) {
            StringBuilder msg = new StringBuilder("I/O error on ");
            msg.append(method.name());
            msg.append(" request for \"");
            String urlString = url.toString();
            int idx = urlString.indexOf(63);
            if (idx != -1) {
                msg.append((CharSequence) urlString, 0, idx);
            } else {
                msg.append(urlString);
            }
            msg.append("\": ");
            msg.append(ex.getMessage());
            return new ResourceAccessException(msg.toString(), ex);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/DefaultRestClient$DefaultResponseSpec.class */
    private class DefaultResponseSpec implements RestClient.ResponseSpec {
        private final HttpRequest clientRequest;
        private final ClientHttpResponse clientResponse;
        private final List<StatusHandler> statusHandlers = new ArrayList(1);
        private final int defaultStatusHandlerCount;

        DefaultResponseSpec(HttpRequest clientRequest, ClientHttpResponse clientResponse) {
            this.clientRequest = clientRequest;
            this.clientResponse = clientResponse;
            this.statusHandlers.addAll(DefaultRestClient.this.defaultStatusHandlers);
            this.statusHandlers.add(StatusHandler.defaultHandler(DefaultRestClient.this.messageConverters));
            this.defaultStatusHandlerCount = this.statusHandlers.size();
        }

        @Override // org.springframework.web.client.RestClient.ResponseSpec
        public RestClient.ResponseSpec onStatus(Predicate<HttpStatusCode> statusPredicate, RestClient.ResponseSpec.ErrorHandler errorHandler) {
            Assert.notNull(statusPredicate, "StatusPredicate must not be null");
            Assert.notNull(errorHandler, "ErrorHandler must not be null");
            return onStatusInternal(StatusHandler.of(statusPredicate, errorHandler));
        }

        @Override // org.springframework.web.client.RestClient.ResponseSpec
        public RestClient.ResponseSpec onStatus(ResponseErrorHandler errorHandler) {
            Assert.notNull(errorHandler, "ResponseErrorHandler must not be null");
            return onStatusInternal(StatusHandler.fromErrorHandler(errorHandler));
        }

        private RestClient.ResponseSpec onStatusInternal(StatusHandler statusHandler) {
            Assert.notNull(statusHandler, "StatusHandler must not be null");
            int index = this.statusHandlers.size() - this.defaultStatusHandlerCount;
            this.statusHandlers.add(index, statusHandler);
            return this;
        }

        @Override // org.springframework.web.client.RestClient.ResponseSpec
        @Nullable
        public <T> T body(Class<T> cls) {
            return (T) readBody(cls, cls);
        }

        @Override // org.springframework.web.client.RestClient.ResponseSpec
        @Nullable
        public <T> T body(ParameterizedTypeReference<T> parameterizedTypeReference) {
            Type type = parameterizedTypeReference.getType();
            return (T) readBody(type, DefaultRestClient.bodyClass(type));
        }

        @Override // org.springframework.web.client.RestClient.ResponseSpec
        public <T> ResponseEntity<T> toEntity(Class<T> bodyType) {
            return toEntityInternal(bodyType, bodyType);
        }

        @Override // org.springframework.web.client.RestClient.ResponseSpec
        public <T> ResponseEntity<T> toEntity(ParameterizedTypeReference<T> bodyType) {
            Type type = bodyType.getType();
            Class<T> bodyClass = DefaultRestClient.bodyClass(type);
            return toEntityInternal(type, bodyClass);
        }

        /* JADX WARN: Multi-variable type inference failed */
        private <T> ResponseEntity<T> toEntityInternal(Type bodyType, Class<T> bodyClass) {
            try {
                return ResponseEntity.status(this.clientResponse.getStatusCode()).headers(this.clientResponse.getHeaders()).body(readBody(bodyType, bodyClass));
            } catch (IOException ex) {
                throw new ResourceAccessException("Could not retrieve response status code: " + ex.getMessage(), ex);
            }
        }

        @Override // org.springframework.web.client.RestClient.ResponseSpec
        public ResponseEntity<Void> toBodilessEntity() {
            try {
                ClientHttpResponse clientHttpResponse = this.clientResponse;
                try {
                    applyStatusHandlers();
                    ResponseEntity build = ResponseEntity.status(this.clientResponse.getStatusCode()).headers(this.clientResponse.getHeaders()).build();
                    if (clientHttpResponse != null) {
                        clientHttpResponse.close();
                    }
                    return build;
                } finally {
                }
            } catch (IOException ex) {
                throw new ResourceAccessException("Could not retrieve response status code: " + ex.getMessage(), ex);
            } catch (UncheckedIOException ex2) {
                throw new ResourceAccessException("Could not retrieve response status code: " + ex2.getMessage(), ex2.getCause());
            }
        }

        @Nullable
        private <T> T readBody(Type type, Class<T> cls) {
            return (T) DefaultRestClient.this.readWithMessageConverters(this.clientResponse, this::applyStatusHandlers, type, cls);
        }

        private void applyStatusHandlers() {
            try {
                ClientHttpResponse response = this.clientResponse;
                if (response instanceof DefaultConvertibleClientHttpResponse) {
                    DefaultConvertibleClientHttpResponse convertibleResponse = (DefaultConvertibleClientHttpResponse) response;
                    response = convertibleResponse.delegate;
                }
                for (StatusHandler handler : this.statusHandlers) {
                    if (handler.test(response)) {
                        handler.handle(this.clientRequest, response);
                        return;
                    }
                }
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/DefaultRestClient$DefaultConvertibleClientHttpResponse.class */
    public class DefaultConvertibleClientHttpResponse implements RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse {
        private final ClientHttpResponse delegate;

        public DefaultConvertibleClientHttpResponse(ClientHttpResponse delegate) {
            this.delegate = delegate;
        }

        @Override // org.springframework.web.client.RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse
        @Nullable
        public <T> T bodyTo(Class<T> cls) {
            return (T) DefaultRestClient.this.readWithMessageConverters(this.delegate, () -> {
            }, cls, cls);
        }

        @Override // org.springframework.web.client.RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse
        @Nullable
        public <T> T bodyTo(ParameterizedTypeReference<T> parameterizedTypeReference) {
            Type type = parameterizedTypeReference.getType();
            return (T) DefaultRestClient.this.readWithMessageConverters(this.delegate, () -> {
            }, type, DefaultRestClient.bodyClass(type));
        }

        @Override // org.springframework.http.HttpInputMessage
        public InputStream getBody() throws IOException {
            return this.delegate.getBody();
        }

        @Override // org.springframework.http.HttpMessage
        public HttpHeaders getHeaders() {
            return this.delegate.getHeaders();
        }

        @Override // org.springframework.http.client.ClientHttpResponse
        public HttpStatusCode getStatusCode() throws IOException {
            return this.delegate.getStatusCode();
        }

        @Override // org.springframework.http.client.ClientHttpResponse
        public String getStatusText() throws IOException {
            return this.delegate.getStatusText();
        }

        @Override // org.springframework.http.client.ClientHttpResponse, java.io.Closeable, java.lang.AutoCloseable
        public void close() {
            this.delegate.close();
        }
    }
}
