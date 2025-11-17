package org.springframework.web.client;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.observation.ClientHttpObservationDocumentation;
import org.springframework.http.client.observation.ClientRequestObservationContext;
import org.springframework.http.client.observation.ClientRequestObservationConvention;
import org.springframework.http.client.observation.DefaultClientRequestObservationConvention;
import org.springframework.http.client.support.InterceptingHttpAccessor;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.cbor.KotlinSerializationCborHttpMessageConverter;
import org.springframework.http.converter.cbor.MappingJackson2CborHttpMessageConverter;
import org.springframework.http.converter.feed.AtomFeedHttpMessageConverter;
import org.springframework.http.converter.feed.RssChannelHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.json.JsonbHttpMessageConverter;
import org.springframework.http.converter.json.KotlinSerializationJsonHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.protobuf.KotlinSerializationProtobufHttpMessageConverter;
import org.springframework.http.converter.smile.MappingJackson2SmileHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriTemplateHandler;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/RestTemplate.class */
public class RestTemplate extends InterceptingHttpAccessor implements RestOperations {
    private static final boolean romePresent;
    private static final boolean jaxb2Present;
    private static final boolean jackson2Present;
    private static final boolean jackson2XmlPresent;
    private static final boolean jackson2SmilePresent;
    private static final boolean jackson2CborPresent;
    private static final boolean gsonPresent;
    private static final boolean jsonbPresent;
    private static final boolean kotlinSerializationCborPresent;
    private static final boolean kotlinSerializationJsonPresent;
    private static final boolean kotlinSerializationProtobufPresent;
    private static final ClientRequestObservationConvention DEFAULT_OBSERVATION_CONVENTION = new DefaultClientRequestObservationConvention();
    private final List<HttpMessageConverter<?>> messageConverters;
    private ResponseErrorHandler errorHandler;
    private UriTemplateHandler uriTemplateHandler;
    private final ResponseExtractor<HttpHeaders> headersExtractor;
    private ObservationRegistry observationRegistry;

    @Nullable
    private ClientRequestObservationConvention observationConvention;

    static {
        ClassLoader classLoader = RestTemplate.class.getClassLoader();
        romePresent = ClassUtils.isPresent("com.rometools.rome.feed.WireFeed", classLoader);
        jaxb2Present = ClassUtils.isPresent("jakarta.xml.bind.Binder", classLoader);
        jackson2Present = ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", classLoader) && ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator", classLoader);
        jackson2XmlPresent = ClassUtils.isPresent("com.fasterxml.jackson.dataformat.xml.XmlMapper", classLoader);
        jackson2SmilePresent = ClassUtils.isPresent("com.fasterxml.jackson.dataformat.smile.SmileFactory", classLoader);
        jackson2CborPresent = ClassUtils.isPresent("com.fasterxml.jackson.dataformat.cbor.CBORFactory", classLoader);
        gsonPresent = ClassUtils.isPresent("com.google.gson.Gson", classLoader);
        jsonbPresent = ClassUtils.isPresent("jakarta.json.bind.Jsonb", classLoader);
        kotlinSerializationCborPresent = ClassUtils.isPresent("kotlinx.serialization.cbor.Cbor", classLoader);
        kotlinSerializationJsonPresent = ClassUtils.isPresent("kotlinx.serialization.json.Json", classLoader);
        kotlinSerializationProtobufPresent = ClassUtils.isPresent("kotlinx.serialization.protobuf.ProtoBuf", classLoader);
    }

    public RestTemplate() {
        this.messageConverters = new ArrayList();
        this.errorHandler = new DefaultResponseErrorHandler();
        this.headersExtractor = new HeadersExtractor();
        this.observationRegistry = ObservationRegistry.NOOP;
        this.messageConverters.add(new ByteArrayHttpMessageConverter());
        this.messageConverters.add(new StringHttpMessageConverter());
        this.messageConverters.add(new ResourceHttpMessageConverter(false));
        this.messageConverters.add(new AllEncompassingFormHttpMessageConverter());
        if (romePresent) {
            this.messageConverters.add(new AtomFeedHttpMessageConverter());
            this.messageConverters.add(new RssChannelHttpMessageConverter());
        }
        if (jackson2XmlPresent) {
            this.messageConverters.add(new MappingJackson2XmlHttpMessageConverter());
        } else if (jaxb2Present) {
            this.messageConverters.add(new Jaxb2RootElementHttpMessageConverter());
        }
        if (kotlinSerializationProtobufPresent) {
            this.messageConverters.add(new KotlinSerializationProtobufHttpMessageConverter());
        }
        if (kotlinSerializationJsonPresent) {
            this.messageConverters.add(new KotlinSerializationJsonHttpMessageConverter());
        }
        if (jackson2Present) {
            this.messageConverters.add(new MappingJackson2HttpMessageConverter());
        } else if (gsonPresent) {
            this.messageConverters.add(new GsonHttpMessageConverter());
        } else if (jsonbPresent) {
            this.messageConverters.add(new JsonbHttpMessageConverter());
        }
        if (jackson2SmilePresent) {
            this.messageConverters.add(new MappingJackson2SmileHttpMessageConverter());
        }
        if (jackson2CborPresent) {
            this.messageConverters.add(new MappingJackson2CborHttpMessageConverter());
        } else if (kotlinSerializationCborPresent) {
            this.messageConverters.add(new KotlinSerializationCborHttpMessageConverter());
        }
        updateErrorHandlerConverters();
        this.uriTemplateHandler = initUriTemplateHandler();
    }

    public RestTemplate(ClientHttpRequestFactory requestFactory) {
        this();
        setRequestFactory(requestFactory);
    }

    public RestTemplate(List<HttpMessageConverter<?>> messageConverters) {
        this.messageConverters = new ArrayList();
        this.errorHandler = new DefaultResponseErrorHandler();
        this.headersExtractor = new HeadersExtractor();
        this.observationRegistry = ObservationRegistry.NOOP;
        validateConverters(messageConverters);
        this.messageConverters.addAll(messageConverters);
        this.uriTemplateHandler = initUriTemplateHandler();
        updateErrorHandlerConverters();
    }

    private void updateErrorHandlerConverters() {
        ResponseErrorHandler responseErrorHandler = this.errorHandler;
        if (responseErrorHandler instanceof DefaultResponseErrorHandler) {
            DefaultResponseErrorHandler handler = (DefaultResponseErrorHandler) responseErrorHandler;
            handler.setMessageConverters(this.messageConverters);
        }
    }

    private static DefaultUriBuilderFactory initUriTemplateHandler() {
        DefaultUriBuilderFactory uriFactory = new DefaultUriBuilderFactory();
        uriFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.URI_COMPONENT);
        return uriFactory;
    }

    public void setMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        validateConverters(messageConverters);
        if (this.messageConverters != messageConverters) {
            this.messageConverters.clear();
            this.messageConverters.addAll(messageConverters);
        }
        updateErrorHandlerConverters();
    }

    private void validateConverters(List<HttpMessageConverter<?>> messageConverters) {
        Assert.notEmpty(messageConverters, "At least one HttpMessageConverter is required");
        Assert.noNullElements(messageConverters, "The HttpMessageConverter list must not contain null elements");
    }

    public List<HttpMessageConverter<?>> getMessageConverters() {
        return this.messageConverters;
    }

    public void setErrorHandler(ResponseErrorHandler errorHandler) {
        Assert.notNull(errorHandler, "ResponseErrorHandler must not be null");
        this.errorHandler = errorHandler;
        updateErrorHandlerConverters();
    }

    public ResponseErrorHandler getErrorHandler() {
        return this.errorHandler;
    }

    public void setDefaultUriVariables(Map<String, ?> uriVars) {
        UriTemplateHandler uriTemplateHandler = this.uriTemplateHandler;
        if (uriTemplateHandler instanceof DefaultUriBuilderFactory) {
            DefaultUriBuilderFactory factory = (DefaultUriBuilderFactory) uriTemplateHandler;
            factory.setDefaultUriVariables(uriVars);
            return;
        }
        throw new IllegalArgumentException("This property is not supported with the configured UriTemplateHandler.");
    }

    public void setUriTemplateHandler(UriTemplateHandler handler) {
        Assert.notNull(handler, "UriTemplateHandler must not be null");
        this.uriTemplateHandler = handler;
    }

    public UriTemplateHandler getUriTemplateHandler() {
        return this.uriTemplateHandler;
    }

    public void setObservationRegistry(ObservationRegistry observationRegistry) {
        Assert.notNull(observationRegistry, "observationRegistry must not be null");
        this.observationRegistry = observationRegistry;
    }

    public ObservationRegistry getObservationRegistry() {
        return this.observationRegistry;
    }

    public void setObservationConvention(ClientRequestObservationConvention observationConvention) {
        Assert.notNull(observationConvention, "observationConvention must not be null");
        this.observationConvention = observationConvention;
    }

    @Nullable
    public ClientRequestObservationConvention getObservationConvention() {
        return this.observationConvention;
    }

    @Override // org.springframework.web.client.RestOperations
    @Nullable
    public <T> T getForObject(String str, Class<T> cls, Object... objArr) throws RestClientException {
        return (T) execute(str, HttpMethod.GET, acceptHeaderRequestCallback(cls), new HttpMessageConverterExtractor(cls, getMessageConverters(), this.logger), objArr);
    }

    @Override // org.springframework.web.client.RestOperations
    @Nullable
    public <T> T getForObject(String str, Class<T> cls, Map<String, ?> map) throws RestClientException {
        return (T) execute(str, HttpMethod.GET, acceptHeaderRequestCallback(cls), new HttpMessageConverterExtractor(cls, getMessageConverters(), this.logger), map);
    }

    @Override // org.springframework.web.client.RestOperations
    @Nullable
    public <T> T getForObject(URI uri, Class<T> cls) throws RestClientException {
        return (T) execute(uri, HttpMethod.GET, acceptHeaderRequestCallback(cls), new HttpMessageConverterExtractor(cls, getMessageConverters(), this.logger));
    }

    @Override // org.springframework.web.client.RestOperations
    public <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType, Object... uriVariables) throws RestClientException {
        RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
        return (ResponseEntity) nonNull((ResponseEntity) execute(url, HttpMethod.GET, requestCallback, responseExtractor, uriVariables));
    }

    @Override // org.springframework.web.client.RestOperations
    public <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
        return (ResponseEntity) nonNull((ResponseEntity) execute(url, HttpMethod.GET, requestCallback, responseExtractor, uriVariables));
    }

    @Override // org.springframework.web.client.RestOperations
    public <T> ResponseEntity<T> getForEntity(URI url, Class<T> responseType) throws RestClientException {
        RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
        return (ResponseEntity) nonNull((ResponseEntity) execute(url, HttpMethod.GET, requestCallback, responseExtractor));
    }

    @Override // org.springframework.web.client.RestOperations
    public HttpHeaders headForHeaders(String url, Object... uriVariables) throws RestClientException {
        return (HttpHeaders) nonNull((HttpHeaders) execute(url, HttpMethod.HEAD, (RequestCallback) null, headersExtractor(), uriVariables));
    }

    @Override // org.springframework.web.client.RestOperations
    public HttpHeaders headForHeaders(String url, Map<String, ?> uriVariables) throws RestClientException {
        return (HttpHeaders) nonNull((HttpHeaders) execute(url, HttpMethod.HEAD, (RequestCallback) null, headersExtractor(), uriVariables));
    }

    @Override // org.springframework.web.client.RestOperations
    public HttpHeaders headForHeaders(URI url) throws RestClientException {
        return (HttpHeaders) nonNull((HttpHeaders) execute(url, HttpMethod.HEAD, null, headersExtractor()));
    }

    @Override // org.springframework.web.client.RestOperations
    @Nullable
    public URI postForLocation(String url, @Nullable Object request, Object... uriVariables) throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(request);
        HttpHeaders headers = (HttpHeaders) execute(url, HttpMethod.POST, requestCallback, headersExtractor(), uriVariables);
        if (headers != null) {
            return headers.getLocation();
        }
        return null;
    }

    @Override // org.springframework.web.client.RestOperations
    @Nullable
    public URI postForLocation(String url, @Nullable Object request, Map<String, ?> uriVariables) throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(request);
        HttpHeaders headers = (HttpHeaders) execute(url, HttpMethod.POST, requestCallback, headersExtractor(), uriVariables);
        if (headers != null) {
            return headers.getLocation();
        }
        return null;
    }

    @Override // org.springframework.web.client.RestOperations
    @Nullable
    public URI postForLocation(URI url, @Nullable Object request) throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(request);
        HttpHeaders headers = (HttpHeaders) execute(url, HttpMethod.POST, requestCallback, headersExtractor());
        if (headers != null) {
            return headers.getLocation();
        }
        return null;
    }

    @Override // org.springframework.web.client.RestOperations
    @Nullable
    public <T> T postForObject(String str, @Nullable Object obj, Class<T> cls, Object... objArr) throws RestClientException {
        return (T) execute(str, HttpMethod.POST, httpEntityCallback(obj, cls), new HttpMessageConverterExtractor(cls, getMessageConverters(), this.logger), objArr);
    }

    @Override // org.springframework.web.client.RestOperations
    @Nullable
    public <T> T postForObject(String str, @Nullable Object obj, Class<T> cls, Map<String, ?> map) throws RestClientException {
        return (T) execute(str, HttpMethod.POST, httpEntityCallback(obj, cls), new HttpMessageConverterExtractor(cls, getMessageConverters(), this.logger), map);
    }

    @Override // org.springframework.web.client.RestOperations
    @Nullable
    public <T> T postForObject(URI uri, @Nullable Object obj, Class<T> cls) throws RestClientException {
        return (T) execute(uri, HttpMethod.POST, httpEntityCallback(obj, cls), new HttpMessageConverterExtractor((Class) cls, getMessageConverters()));
    }

    @Override // org.springframework.web.client.RestOperations
    public <T> ResponseEntity<T> postForEntity(String url, @Nullable Object request, Class<T> responseType, Object... uriVariables) throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(request, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
        return (ResponseEntity) nonNull((ResponseEntity) execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables));
    }

    @Override // org.springframework.web.client.RestOperations
    public <T> ResponseEntity<T> postForEntity(String url, @Nullable Object request, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(request, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
        return (ResponseEntity) nonNull((ResponseEntity) execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables));
    }

    @Override // org.springframework.web.client.RestOperations
    public <T> ResponseEntity<T> postForEntity(URI url, @Nullable Object request, Class<T> responseType) throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(request, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
        return (ResponseEntity) nonNull((ResponseEntity) execute(url, HttpMethod.POST, requestCallback, responseExtractor));
    }

    @Override // org.springframework.web.client.RestOperations
    public void put(String url, @Nullable Object request, Object... uriVariables) throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(request);
        execute(url, HttpMethod.PUT, requestCallback, (ResponseExtractor) null, uriVariables);
    }

    @Override // org.springframework.web.client.RestOperations
    public void put(String url, @Nullable Object request, Map<String, ?> uriVariables) throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(request);
        execute(url, HttpMethod.PUT, requestCallback, (ResponseExtractor) null, uriVariables);
    }

    @Override // org.springframework.web.client.RestOperations
    public void put(URI url, @Nullable Object request) throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(request);
        execute(url, HttpMethod.PUT, requestCallback, null);
    }

    @Override // org.springframework.web.client.RestOperations
    @Nullable
    public <T> T patchForObject(String str, @Nullable Object obj, Class<T> cls, Object... objArr) throws RestClientException {
        return (T) execute(str, HttpMethod.PATCH, httpEntityCallback(obj, cls), new HttpMessageConverterExtractor(cls, getMessageConverters(), this.logger), objArr);
    }

    @Override // org.springframework.web.client.RestOperations
    @Nullable
    public <T> T patchForObject(String str, @Nullable Object obj, Class<T> cls, Map<String, ?> map) throws RestClientException {
        return (T) execute(str, HttpMethod.PATCH, httpEntityCallback(obj, cls), new HttpMessageConverterExtractor(cls, getMessageConverters(), this.logger), map);
    }

    @Override // org.springframework.web.client.RestOperations
    @Nullable
    public <T> T patchForObject(URI uri, @Nullable Object obj, Class<T> cls) throws RestClientException {
        return (T) execute(uri, HttpMethod.PATCH, httpEntityCallback(obj, cls), new HttpMessageConverterExtractor((Class) cls, getMessageConverters()));
    }

    @Override // org.springframework.web.client.RestOperations
    public void delete(String url, Object... uriVariables) throws RestClientException {
        execute(url, HttpMethod.DELETE, (RequestCallback) null, (ResponseExtractor) null, uriVariables);
    }

    @Override // org.springframework.web.client.RestOperations
    public void delete(String url, Map<String, ?> uriVariables) throws RestClientException {
        execute(url, HttpMethod.DELETE, (RequestCallback) null, (ResponseExtractor) null, uriVariables);
    }

    @Override // org.springframework.web.client.RestOperations
    public void delete(URI url) throws RestClientException {
        execute(url, HttpMethod.DELETE, null, null);
    }

    @Override // org.springframework.web.client.RestOperations
    public Set<HttpMethod> optionsForAllow(String url, Object... uriVariables) throws RestClientException {
        ResponseExtractor<HttpHeaders> headersExtractor = headersExtractor();
        HttpHeaders headers = (HttpHeaders) execute(url, HttpMethod.OPTIONS, (RequestCallback) null, headersExtractor, uriVariables);
        return headers != null ? headers.getAllow() : Collections.emptySet();
    }

    @Override // org.springframework.web.client.RestOperations
    public Set<HttpMethod> optionsForAllow(String url, Map<String, ?> uriVariables) throws RestClientException {
        ResponseExtractor<HttpHeaders> headersExtractor = headersExtractor();
        HttpHeaders headers = (HttpHeaders) execute(url, HttpMethod.OPTIONS, (RequestCallback) null, headersExtractor, uriVariables);
        return headers != null ? headers.getAllow() : Collections.emptySet();
    }

    @Override // org.springframework.web.client.RestOperations
    public Set<HttpMethod> optionsForAllow(URI url) throws RestClientException {
        ResponseExtractor<HttpHeaders> headersExtractor = headersExtractor();
        HttpHeaders headers = (HttpHeaders) execute(url, HttpMethod.OPTIONS, null, headersExtractor);
        return headers != null ? headers.getAllow() : Collections.emptySet();
    }

    @Override // org.springframework.web.client.RestOperations
    public <T> ResponseEntity<T> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables) throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
        return (ResponseEntity) nonNull((ResponseEntity) execute(url, method, requestCallback, responseExtractor, uriVariables));
    }

    @Override // org.springframework.web.client.RestOperations
    public <T> ResponseEntity<T> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
        return (ResponseEntity) nonNull((ResponseEntity) execute(url, method, requestCallback, responseExtractor, uriVariables));
    }

    @Override // org.springframework.web.client.RestOperations
    public <T> ResponseEntity<T> exchange(URI url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, Class<T> responseType) throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
        return (ResponseEntity) nonNull((ResponseEntity) execute(url, method, requestCallback, responseExtractor));
    }

    @Override // org.springframework.web.client.RestOperations
    public <T> ResponseEntity<T> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType, Object... uriVariables) throws RestClientException {
        Type type = responseType.getType();
        RequestCallback requestCallback = httpEntityCallback(requestEntity, type);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
        return (ResponseEntity) nonNull((ResponseEntity) execute(url, method, requestCallback, responseExtractor, uriVariables));
    }

    @Override // org.springframework.web.client.RestOperations
    public <T> ResponseEntity<T> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        Type type = responseType.getType();
        RequestCallback requestCallback = httpEntityCallback(requestEntity, type);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
        return (ResponseEntity) nonNull((ResponseEntity) execute(url, method, requestCallback, responseExtractor, uriVariables));
    }

    @Override // org.springframework.web.client.RestOperations
    public <T> ResponseEntity<T> exchange(URI url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType) throws RestClientException {
        Type type = responseType.getType();
        RequestCallback requestCallback = httpEntityCallback(requestEntity, type);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
        return (ResponseEntity) nonNull((ResponseEntity) execute(url, method, requestCallback, responseExtractor));
    }

    @Override // org.springframework.web.client.RestOperations
    public <T> ResponseEntity<T> exchange(RequestEntity<?> entity, Class<T> responseType) throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(entity, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
        return (ResponseEntity) nonNull((ResponseEntity) doExecute(resolveUrl(entity), resolveUriTemplate(entity), entity.getMethod(), requestCallback, responseExtractor));
    }

    @Override // org.springframework.web.client.RestOperations
    public <T> ResponseEntity<T> exchange(RequestEntity<?> entity, ParameterizedTypeReference<T> responseType) throws RestClientException {
        Type type = responseType.getType();
        RequestCallback requestCallback = httpEntityCallback(entity, type);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
        return (ResponseEntity) nonNull((ResponseEntity) doExecute(resolveUrl(entity), resolveUriTemplate(entity), entity.getMethod(), requestCallback, responseExtractor));
    }

    private URI resolveUrl(RequestEntity<?> entity) {
        if (entity instanceof RequestEntity.UriTemplateRequestEntity) {
            RequestEntity.UriTemplateRequestEntity<?> ext = (RequestEntity.UriTemplateRequestEntity) entity;
            if (ext.getVars() != null) {
                return this.uriTemplateHandler.expand(ext.getUriTemplate(), ext.getVars());
            }
            if (ext.getVarsMap() != null) {
                return this.uriTemplateHandler.expand(ext.getUriTemplate(), ext.getVarsMap());
            }
            throw new IllegalStateException("No variables specified for URI template: " + ext.getUriTemplate());
        }
        return entity.getUrl();
    }

    @Nullable
    private String resolveUriTemplate(RequestEntity<?> entity) {
        if (entity instanceof RequestEntity.UriTemplateRequestEntity) {
            RequestEntity.UriTemplateRequestEntity<?> templated = (RequestEntity.UriTemplateRequestEntity) entity;
            return templated.getUriTemplate();
        }
        return null;
    }

    @Override // org.springframework.web.client.RestOperations
    @Nullable
    public <T> T execute(String str, HttpMethod httpMethod, @Nullable RequestCallback requestCallback, @Nullable ResponseExtractor<T> responseExtractor, Object... objArr) throws RestClientException {
        return (T) doExecute(getUriTemplateHandler().expand(str, objArr), str, httpMethod, requestCallback, responseExtractor);
    }

    @Override // org.springframework.web.client.RestOperations
    @Nullable
    public <T> T execute(String str, HttpMethod httpMethod, @Nullable RequestCallback requestCallback, @Nullable ResponseExtractor<T> responseExtractor, Map<String, ?> map) throws RestClientException {
        return (T) doExecute(getUriTemplateHandler().expand(str, map), str, httpMethod, requestCallback, responseExtractor);
    }

    @Override // org.springframework.web.client.RestOperations
    @Nullable
    public <T> T execute(URI uri, HttpMethod httpMethod, @Nullable RequestCallback requestCallback, @Nullable ResponseExtractor<T> responseExtractor) throws RestClientException {
        return (T) doExecute(uri, null, httpMethod, requestCallback, responseExtractor);
    }

    @Nullable
    @Deprecated
    protected <T> T doExecute(URI uri, @Nullable HttpMethod httpMethod, @Nullable RequestCallback requestCallback, @Nullable ResponseExtractor<T> responseExtractor) throws RestClientException {
        return (T) doExecute(uri, null, httpMethod, requestCallback, responseExtractor);
    }

    @Nullable
    protected <T> T doExecute(URI url, @Nullable String uriTemplate, @Nullable HttpMethod method, @Nullable RequestCallback requestCallback, @Nullable ResponseExtractor<T> responseExtractor) throws RestClientException {
        Assert.notNull(url, "url is required");
        Assert.notNull(method, "HttpMethod is required");
        try {
            ClientHttpRequest request = createRequest(url, method);
            ClientRequestObservationContext observationContext = new ClientRequestObservationContext(request);
            observationContext.setUriTemplate(uriTemplate);
            Observation observation = ClientHttpObservationDocumentation.HTTP_CLIENT_EXCHANGES.observation(this.observationConvention, DEFAULT_OBSERVATION_CONVENTION, () -> {
                return observationContext;
            }, this.observationRegistry).start();
            ClientHttpResponse response = null;
            try {
                if (requestCallback != null) {
                    try {
                        try {
                            requestCallback.doWithRequest(request);
                        } catch (RestClientException exc) {
                            observation.error(exc);
                            throw exc;
                        }
                    } catch (IOException ex) {
                        ResourceAccessException exception = createResourceAccessException(url, method, ex);
                        observation.error(exception);
                        throw exception;
                    }
                }
                response = request.execute();
                observationContext.setResponse(response);
                handleResponse(url, method, response);
                T extractData = responseExtractor != null ? responseExtractor.extractData(response) : null;
                if (response != null) {
                    response.close();
                }
                observation.stop();
                return extractData;
            } catch (Throwable th) {
                if (response != null) {
                    response.close();
                }
                observation.stop();
                throw th;
            }
        } catch (IOException ex2) {
            throw createResourceAccessException(url, method, ex2);
        }
    }

    private static ResourceAccessException createResourceAccessException(URI url, HttpMethod method, IOException ex) {
        String resource = url.toString();
        return new ResourceAccessException("I/O error on " + method.name() + " request for \"" + (url.getRawQuery() != null ? resource.substring(0, resource.indexOf(63)) : resource) + "\": " + ex.getMessage(), ex);
    }

    protected void handleResponse(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        ResponseErrorHandler errorHandler = getErrorHandler();
        boolean hasError = errorHandler.hasError(response);
        if (this.logger.isDebugEnabled()) {
            try {
                HttpStatusCode statusCode = response.getStatusCode();
                this.logger.debug("Response " + statusCode);
            } catch (IOException ex) {
                this.logger.debug("Failed to obtain response status code", ex);
            }
        }
        if (hasError) {
            errorHandler.handleError(url, method, response);
        }
    }

    public <T> RequestCallback acceptHeaderRequestCallback(Class<T> responseType) {
        return new AcceptHeaderRequestCallback(responseType);
    }

    public <T> RequestCallback httpEntityCallback(@Nullable Object requestBody) {
        return new HttpEntityRequestCallback(this, requestBody);
    }

    public <T> RequestCallback httpEntityCallback(@Nullable Object requestBody, Type responseType) {
        return new HttpEntityRequestCallback(requestBody, responseType);
    }

    public <T> ResponseExtractor<ResponseEntity<T>> responseEntityExtractor(Type responseType) {
        return new ResponseEntityResponseExtractor(responseType);
    }

    protected ResponseExtractor<HttpHeaders> headersExtractor() {
        return this.headersExtractor;
    }

    private static <T> T nonNull(@Nullable T result) {
        Assert.state(result != null, "No result");
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/RestTemplate$AcceptHeaderRequestCallback.class */
    public class AcceptHeaderRequestCallback implements RequestCallback {

        @Nullable
        private final Type responseType;

        public AcceptHeaderRequestCallback(@Nullable Type responseType) {
            this.responseType = responseType;
        }

        @Override // org.springframework.web.client.RequestCallback
        public void doWithRequest(ClientHttpRequest request) throws IOException {
            if (this.responseType != null) {
                List<MediaType> allSupportedMediaTypes = (List) RestTemplate.this.getMessageConverters().stream().filter(converter -> {
                    return canReadResponse(this.responseType, converter);
                }).flatMap(converter2 -> {
                    return getSupportedMediaTypes(this.responseType, converter2);
                }).distinct().collect(Collectors.toList());
                MimeTypeUtils.sortBySpecificity(allSupportedMediaTypes);
                if (RestTemplate.this.logger.isDebugEnabled()) {
                    RestTemplate.this.logger.debug("Accept=" + allSupportedMediaTypes);
                }
                request.getHeaders().setAccept(allSupportedMediaTypes);
            }
        }

        private boolean canReadResponse(Type responseType, HttpMessageConverter<?> converter) {
            Class<?> cls;
            if (responseType instanceof Class) {
                Class<?> clazz = (Class) responseType;
                cls = clazz;
            } else {
                cls = null;
            }
            Class<?> responseClass = cls;
            if (responseClass != null) {
                return converter.canRead(responseClass, null);
            }
            if (converter instanceof GenericHttpMessageConverter) {
                GenericHttpMessageConverter<?> genericConverter = (GenericHttpMessageConverter) converter;
                return genericConverter.canRead(responseType, null, null);
            }
            return false;
        }

        private Stream<MediaType> getSupportedMediaTypes(Type type, HttpMessageConverter<?> converter) {
            Type type2;
            Class<?> cls;
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                type2 = parameterizedType.getRawType();
            } else {
                type2 = type;
            }
            Type rawType = type2;
            if (rawType instanceof Class) {
                Class<?> rawClass = (Class) rawType;
                cls = rawClass;
            } else {
                cls = null;
            }
            Class<?> clazz = cls;
            return (clazz != null ? converter.getSupportedMediaTypes(clazz) : converter.getSupportedMediaTypes()).stream().map(mediaType -> {
                if (mediaType.getCharset() != null) {
                    return new MediaType(mediaType.getType(), mediaType.getSubtype());
                }
                return mediaType;
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/RestTemplate$HttpEntityRequestCallback.class */
    public class HttpEntityRequestCallback extends AcceptHeaderRequestCallback {
        private final HttpEntity<?> requestEntity;

        public HttpEntityRequestCallback(@Nullable final RestTemplate this$0, Object requestBody) {
            this(requestBody, null);
        }

        public HttpEntityRequestCallback(@Nullable Object requestBody, @Nullable Type responseType) {
            super(responseType);
            if (requestBody instanceof HttpEntity) {
                HttpEntity<?> httpEntity = (HttpEntity) requestBody;
                this.requestEntity = httpEntity;
            } else if (requestBody != null) {
                this.requestEntity = new HttpEntity<>(requestBody);
            } else {
                this.requestEntity = HttpEntity.EMPTY;
            }
        }

        @Override // org.springframework.web.client.RestTemplate.AcceptHeaderRequestCallback, org.springframework.web.client.RequestCallback
        public void doWithRequest(ClientHttpRequest httpRequest) throws IOException {
            Type type;
            super.doWithRequest(httpRequest);
            Object requestBody = this.requestEntity.getBody();
            if (requestBody == null) {
                HttpHeaders httpHeaders = httpRequest.getHeaders();
                HttpHeaders requestHeaders = this.requestEntity.getHeaders();
                if (!requestHeaders.isEmpty()) {
                    requestHeaders.forEach((key, values) -> {
                        httpHeaders.put(key, (List<String>) new ArrayList(values));
                    });
                }
                if (httpHeaders.getContentLength() < 0) {
                    httpHeaders.setContentLength(0L);
                    return;
                }
                return;
            }
            Class<?> requestBodyClass = requestBody.getClass();
            HttpEntity<?> httpEntity = this.requestEntity;
            if (httpEntity instanceof RequestEntity) {
                RequestEntity<?> _requestEntity = (RequestEntity) httpEntity;
                type = _requestEntity.getType();
            } else {
                type = requestBodyClass;
            }
            Type requestBodyType = type;
            HttpHeaders httpHeaders2 = httpRequest.getHeaders();
            HttpHeaders requestHeaders2 = this.requestEntity.getHeaders();
            MediaType requestContentType = requestHeaders2.getContentType();
            for (HttpMessageConverter<?> messageConverter : RestTemplate.this.getMessageConverters()) {
                if (messageConverter instanceof GenericHttpMessageConverter) {
                    GenericHttpMessageConverter genericConverter = (GenericHttpMessageConverter) messageConverter;
                    if (genericConverter.canWrite(requestBodyType, requestBodyClass, requestContentType)) {
                        if (!requestHeaders2.isEmpty()) {
                            requestHeaders2.forEach((key2, values2) -> {
                                httpHeaders2.put(key2, (List<String>) new ArrayList(values2));
                            });
                        }
                        logBody(requestBody, requestContentType, genericConverter);
                        genericConverter.write(requestBody, requestBodyType, requestContentType, httpRequest);
                        return;
                    }
                } else if (messageConverter.canWrite(requestBodyClass, requestContentType)) {
                    if (!requestHeaders2.isEmpty()) {
                        requestHeaders2.forEach((key3, values3) -> {
                            httpHeaders2.put(key3, (List<String>) new ArrayList(values3));
                        });
                    }
                    logBody(requestBody, requestContentType, messageConverter);
                    messageConverter.write(requestBody, requestContentType, httpRequest);
                    return;
                }
            }
            String message = "No HttpMessageConverter for " + requestBodyClass.getName();
            if (requestContentType != null) {
                message = message + " and content type \"" + requestContentType + "\"";
            }
            throw new RestClientException(message);
        }

        private void logBody(Object body, @Nullable MediaType mediaType, HttpMessageConverter<?> converter) {
            if (RestTemplate.this.logger.isDebugEnabled()) {
                if (mediaType != null) {
                    RestTemplate.this.logger.debug("Writing [" + body + "] as \"" + mediaType + "\"");
                } else {
                    RestTemplate.this.logger.debug("Writing [" + body + "] with " + converter.getClass().getName());
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/RestTemplate$ResponseEntityResponseExtractor.class */
    public class ResponseEntityResponseExtractor<T> implements ResponseExtractor<ResponseEntity<T>> {

        @Nullable
        private final HttpMessageConverterExtractor<T> delegate;

        public ResponseEntityResponseExtractor(@Nullable Type responseType) {
            if (responseType != null && Void.class != responseType) {
                this.delegate = new HttpMessageConverterExtractor<>(responseType, RestTemplate.this.getMessageConverters(), RestTemplate.this.logger);
            } else {
                this.delegate = null;
            }
        }

        @Override // org.springframework.web.client.ResponseExtractor
        public ResponseEntity<T> extractData(ClientHttpResponse response) throws IOException {
            if (this.delegate != null) {
                T body = this.delegate.extractData(response);
                return ResponseEntity.status(response.getStatusCode()).headers(response.getHeaders()).body(body);
            }
            return ResponseEntity.status(response.getStatusCode()).headers(response.getHeaders()).build();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/RestTemplate$HeadersExtractor.class */
    private static class HeadersExtractor implements ResponseExtractor<HttpHeaders> {
        private HeadersExtractor() {
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.web.client.ResponseExtractor
        public HttpHeaders extractData(ClientHttpResponse response) {
            return response.getHeaders();
        }
    }
}
