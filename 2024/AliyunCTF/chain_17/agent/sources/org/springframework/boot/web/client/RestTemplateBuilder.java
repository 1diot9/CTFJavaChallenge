package org.springframework.boot.web.client;

import java.nio.charset.Charset;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplateHandler;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/client/RestTemplateBuilder.class */
public class RestTemplateBuilder {
    private final ClientHttpRequestFactorySettings requestFactorySettings;
    private final boolean detectRequestFactory;
    private final String rootUri;
    private final Set<HttpMessageConverter<?>> messageConverters;
    private final Set<ClientHttpRequestInterceptor> interceptors;
    private final Function<ClientHttpRequestFactorySettings, ClientHttpRequestFactory> requestFactory;
    private final UriTemplateHandler uriTemplateHandler;
    private final ResponseErrorHandler errorHandler;
    private final BasicAuthentication basicAuthentication;
    private final Map<String, List<String>> defaultHeaders;
    private final Set<RestTemplateCustomizer> customizers;
    private final Set<RestTemplateRequestCustomizer<?>> requestCustomizers;

    public RestTemplateBuilder(RestTemplateCustomizer... customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        this.requestFactorySettings = ClientHttpRequestFactorySettings.DEFAULTS;
        this.detectRequestFactory = true;
        this.rootUri = null;
        this.messageConverters = null;
        this.interceptors = Collections.emptySet();
        this.requestFactory = null;
        this.uriTemplateHandler = null;
        this.errorHandler = null;
        this.basicAuthentication = null;
        this.defaultHeaders = Collections.emptyMap();
        this.customizers = copiedSetOf(customizers);
        this.requestCustomizers = Collections.emptySet();
    }

    private RestTemplateBuilder(ClientHttpRequestFactorySettings requestFactorySettings, boolean detectRequestFactory, String rootUri, Set<HttpMessageConverter<?>> messageConverters, Set<ClientHttpRequestInterceptor> interceptors, Function<ClientHttpRequestFactorySettings, ClientHttpRequestFactory> requestFactory, UriTemplateHandler uriTemplateHandler, ResponseErrorHandler errorHandler, BasicAuthentication basicAuthentication, Map<String, List<String>> defaultHeaders, Set<RestTemplateCustomizer> customizers, Set<RestTemplateRequestCustomizer<?>> requestCustomizers) {
        this.requestFactorySettings = requestFactorySettings;
        this.detectRequestFactory = detectRequestFactory;
        this.rootUri = rootUri;
        this.messageConverters = messageConverters;
        this.interceptors = interceptors;
        this.requestFactory = requestFactory;
        this.uriTemplateHandler = uriTemplateHandler;
        this.errorHandler = errorHandler;
        this.basicAuthentication = basicAuthentication;
        this.defaultHeaders = defaultHeaders;
        this.customizers = customizers;
        this.requestCustomizers = requestCustomizers;
    }

    public RestTemplateBuilder detectRequestFactory(boolean detectRequestFactory) {
        return new RestTemplateBuilder(this.requestFactorySettings, detectRequestFactory, this.rootUri, this.messageConverters, this.interceptors, this.requestFactory, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.defaultHeaders, this.customizers, this.requestCustomizers);
    }

    public RestTemplateBuilder rootUri(String rootUri) {
        return new RestTemplateBuilder(this.requestFactorySettings, this.detectRequestFactory, rootUri, this.messageConverters, this.interceptors, this.requestFactory, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.defaultHeaders, this.customizers, this.requestCustomizers);
    }

    public RestTemplateBuilder messageConverters(HttpMessageConverter<?>... messageConverters) {
        Assert.notNull(messageConverters, "MessageConverters must not be null");
        return messageConverters(Arrays.asList(messageConverters));
    }

    public RestTemplateBuilder messageConverters(Collection<? extends HttpMessageConverter<?>> messageConverters) {
        Assert.notNull(messageConverters, "MessageConverters must not be null");
        return new RestTemplateBuilder(this.requestFactorySettings, this.detectRequestFactory, this.rootUri, copiedSetOf(messageConverters), this.interceptors, this.requestFactory, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.defaultHeaders, this.customizers, this.requestCustomizers);
    }

    public RestTemplateBuilder additionalMessageConverters(HttpMessageConverter<?>... messageConverters) {
        Assert.notNull(messageConverters, "MessageConverters must not be null");
        return additionalMessageConverters(Arrays.asList(messageConverters));
    }

    public RestTemplateBuilder additionalMessageConverters(Collection<? extends HttpMessageConverter<?>> messageConverters) {
        Assert.notNull(messageConverters, "MessageConverters must not be null");
        return new RestTemplateBuilder(this.requestFactorySettings, this.detectRequestFactory, this.rootUri, append(this.messageConverters, messageConverters), this.interceptors, this.requestFactory, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.defaultHeaders, this.customizers, this.requestCustomizers);
    }

    public RestTemplateBuilder defaultMessageConverters() {
        return new RestTemplateBuilder(this.requestFactorySettings, this.detectRequestFactory, this.rootUri, copiedSetOf(new RestTemplate().getMessageConverters()), this.interceptors, this.requestFactory, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.defaultHeaders, this.customizers, this.requestCustomizers);
    }

    public RestTemplateBuilder interceptors(ClientHttpRequestInterceptor... interceptors) {
        Assert.notNull(interceptors, "interceptors must not be null");
        return interceptors(Arrays.asList(interceptors));
    }

    public RestTemplateBuilder interceptors(Collection<ClientHttpRequestInterceptor> interceptors) {
        Assert.notNull(interceptors, "interceptors must not be null");
        return new RestTemplateBuilder(this.requestFactorySettings, this.detectRequestFactory, this.rootUri, this.messageConverters, copiedSetOf(interceptors), this.requestFactory, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.defaultHeaders, this.customizers, this.requestCustomizers);
    }

    public RestTemplateBuilder additionalInterceptors(ClientHttpRequestInterceptor... interceptors) {
        Assert.notNull(interceptors, "interceptors must not be null");
        return additionalInterceptors(Arrays.asList(interceptors));
    }

    public RestTemplateBuilder additionalInterceptors(Collection<? extends ClientHttpRequestInterceptor> interceptors) {
        Assert.notNull(interceptors, "interceptors must not be null");
        return new RestTemplateBuilder(this.requestFactorySettings, this.detectRequestFactory, this.rootUri, this.messageConverters, append(this.interceptors, interceptors), this.requestFactory, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.defaultHeaders, this.customizers, this.requestCustomizers);
    }

    public RestTemplateBuilder requestFactory(Class<? extends ClientHttpRequestFactory> requestFactoryType) {
        Assert.notNull(requestFactoryType, "RequestFactoryType must not be null");
        return requestFactory(settings -> {
            return ClientHttpRequestFactories.get(requestFactoryType, settings);
        });
    }

    public RestTemplateBuilder requestFactory(Supplier<ClientHttpRequestFactory> requestFactorySupplier) {
        Assert.notNull(requestFactorySupplier, "RequestFactorySupplier must not be null");
        return requestFactory(settings -> {
            return ClientHttpRequestFactories.get(requestFactorySupplier, settings);
        });
    }

    public RestTemplateBuilder requestFactory(Function<ClientHttpRequestFactorySettings, ClientHttpRequestFactory> requestFactoryFunction) {
        Assert.notNull(requestFactoryFunction, "RequestFactoryFunction must not be null");
        return new RestTemplateBuilder(this.requestFactorySettings, this.detectRequestFactory, this.rootUri, this.messageConverters, this.interceptors, requestFactoryFunction, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.defaultHeaders, this.customizers, this.requestCustomizers);
    }

    public RestTemplateBuilder uriTemplateHandler(UriTemplateHandler uriTemplateHandler) {
        Assert.notNull(uriTemplateHandler, "UriTemplateHandler must not be null");
        return new RestTemplateBuilder(this.requestFactorySettings, this.detectRequestFactory, this.rootUri, this.messageConverters, this.interceptors, this.requestFactory, uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.defaultHeaders, this.customizers, this.requestCustomizers);
    }

    public RestTemplateBuilder errorHandler(ResponseErrorHandler errorHandler) {
        Assert.notNull(errorHandler, "ErrorHandler must not be null");
        return new RestTemplateBuilder(this.requestFactorySettings, this.detectRequestFactory, this.rootUri, this.messageConverters, this.interceptors, this.requestFactory, this.uriTemplateHandler, errorHandler, this.basicAuthentication, this.defaultHeaders, this.customizers, this.requestCustomizers);
    }

    public RestTemplateBuilder basicAuthentication(String username, String password) {
        return basicAuthentication(username, password, null);
    }

    public RestTemplateBuilder basicAuthentication(String username, String password, Charset charset) {
        return new RestTemplateBuilder(this.requestFactorySettings, this.detectRequestFactory, this.rootUri, this.messageConverters, this.interceptors, this.requestFactory, this.uriTemplateHandler, this.errorHandler, new BasicAuthentication(username, password, charset), this.defaultHeaders, this.customizers, this.requestCustomizers);
    }

    public RestTemplateBuilder defaultHeader(String name, String... values) {
        Assert.notNull(name, "Name must not be null");
        Assert.notNull(values, "Values must not be null");
        return new RestTemplateBuilder(this.requestFactorySettings, this.detectRequestFactory, this.rootUri, this.messageConverters, this.interceptors, this.requestFactory, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, append(this.defaultHeaders, name, values), this.customizers, this.requestCustomizers);
    }

    public RestTemplateBuilder setConnectTimeout(Duration connectTimeout) {
        return new RestTemplateBuilder(this.requestFactorySettings.withConnectTimeout(connectTimeout), this.detectRequestFactory, this.rootUri, this.messageConverters, this.interceptors, this.requestFactory, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.defaultHeaders, this.customizers, this.requestCustomizers);
    }

    public RestTemplateBuilder setReadTimeout(Duration readTimeout) {
        return new RestTemplateBuilder(this.requestFactorySettings.withReadTimeout(readTimeout), this.detectRequestFactory, this.rootUri, this.messageConverters, this.interceptors, this.requestFactory, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.defaultHeaders, this.customizers, this.requestCustomizers);
    }

    @Deprecated(since = "3.2.0", forRemoval = true)
    public RestTemplateBuilder setBufferRequestBody(boolean bufferRequestBody) {
        return this;
    }

    public RestTemplateBuilder setSslBundle(SslBundle sslBundle) {
        return new RestTemplateBuilder(this.requestFactorySettings.withSslBundle(sslBundle), this.detectRequestFactory, this.rootUri, this.messageConverters, this.interceptors, this.requestFactory, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.defaultHeaders, this.customizers, this.requestCustomizers);
    }

    public RestTemplateBuilder customizers(RestTemplateCustomizer... customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        return customizers(Arrays.asList(customizers));
    }

    public RestTemplateBuilder customizers(Collection<? extends RestTemplateCustomizer> customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        return new RestTemplateBuilder(this.requestFactorySettings, this.detectRequestFactory, this.rootUri, this.messageConverters, this.interceptors, this.requestFactory, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.defaultHeaders, copiedSetOf(customizers), this.requestCustomizers);
    }

    public RestTemplateBuilder additionalCustomizers(RestTemplateCustomizer... customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        return additionalCustomizers(Arrays.asList(customizers));
    }

    public RestTemplateBuilder additionalCustomizers(Collection<? extends RestTemplateCustomizer> customizers) {
        Assert.notNull(customizers, "RestTemplateCustomizers must not be null");
        return new RestTemplateBuilder(this.requestFactorySettings, this.detectRequestFactory, this.rootUri, this.messageConverters, this.interceptors, this.requestFactory, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.defaultHeaders, append(this.customizers, customizers), this.requestCustomizers);
    }

    public RestTemplateBuilder requestCustomizers(RestTemplateRequestCustomizer<?>... requestCustomizers) {
        Assert.notNull(requestCustomizers, "RequestCustomizers must not be null");
        return requestCustomizers(Arrays.asList(requestCustomizers));
    }

    public RestTemplateBuilder requestCustomizers(Collection<? extends RestTemplateRequestCustomizer<?>> requestCustomizers) {
        Assert.notNull(requestCustomizers, "RequestCustomizers must not be null");
        return new RestTemplateBuilder(this.requestFactorySettings, this.detectRequestFactory, this.rootUri, this.messageConverters, this.interceptors, this.requestFactory, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.defaultHeaders, this.customizers, copiedSetOf(requestCustomizers));
    }

    public RestTemplateBuilder additionalRequestCustomizers(RestTemplateRequestCustomizer<?>... requestCustomizers) {
        Assert.notNull(requestCustomizers, "RequestCustomizers must not be null");
        return additionalRequestCustomizers(Arrays.asList(requestCustomizers));
    }

    public RestTemplateBuilder additionalRequestCustomizers(Collection<? extends RestTemplateRequestCustomizer<?>> requestCustomizers) {
        Assert.notNull(requestCustomizers, "RequestCustomizers must not be null");
        return new RestTemplateBuilder(this.requestFactorySettings, this.detectRequestFactory, this.rootUri, this.messageConverters, this.interceptors, this.requestFactory, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.defaultHeaders, this.customizers, append(this.requestCustomizers, requestCustomizers));
    }

    public RestTemplate build() {
        return configure(new RestTemplate());
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <T extends RestTemplate> T build(Class<T> cls) {
        return (T) configure((RestTemplate) BeanUtils.instantiateClass(cls));
    }

    public <T extends RestTemplate> T configure(T restTemplate) {
        ClientHttpRequestFactory requestFactory = buildRequestFactory();
        if (requestFactory != null) {
            restTemplate.setRequestFactory(requestFactory);
        }
        addClientHttpRequestInitializer(restTemplate);
        if (!CollectionUtils.isEmpty(this.messageConverters)) {
            restTemplate.setMessageConverters(new ArrayList(this.messageConverters));
        }
        if (this.uriTemplateHandler != null) {
            restTemplate.setUriTemplateHandler(this.uriTemplateHandler);
        }
        if (this.errorHandler != null) {
            restTemplate.setErrorHandler(this.errorHandler);
        }
        if (this.rootUri != null) {
            RootUriTemplateHandler.addTo(restTemplate, this.rootUri);
        }
        restTemplate.getInterceptors().addAll(this.interceptors);
        if (!CollectionUtils.isEmpty(this.customizers)) {
            for (RestTemplateCustomizer customizer : this.customizers) {
                customizer.customize(restTemplate);
            }
        }
        return restTemplate;
    }

    public ClientHttpRequestFactory buildRequestFactory() {
        if (this.requestFactory != null) {
            return this.requestFactory.apply(this.requestFactorySettings);
        }
        if (this.detectRequestFactory) {
            return ClientHttpRequestFactories.get(this.requestFactorySettings);
        }
        return null;
    }

    private void addClientHttpRequestInitializer(RestTemplate restTemplate) {
        if (this.basicAuthentication == null && this.defaultHeaders.isEmpty() && this.requestCustomizers.isEmpty()) {
            return;
        }
        restTemplate.getClientHttpRequestInitializers().add(new RestTemplateBuilderClientHttpRequestInitializer(this.basicAuthentication, this.defaultHeaders, this.requestCustomizers));
    }

    private <T> Set<T> copiedSetOf(T... items) {
        return copiedSetOf(Arrays.asList(items));
    }

    private <T> Set<T> copiedSetOf(Collection<? extends T> collection) {
        return Collections.unmodifiableSet(new LinkedHashSet(collection));
    }

    private static <T> List<T> copiedListOf(T[] items) {
        return Collections.unmodifiableList(Arrays.asList(Arrays.copyOf(items, items.length)));
    }

    private static <T> Set<T> append(Collection<? extends T> collection, Collection<? extends T> additions) {
        Set<T> result = new LinkedHashSet<>(collection != null ? collection : Collections.emptySet());
        if (additions != null) {
            result.addAll(additions);
        }
        return Collections.unmodifiableSet(result);
    }

    private static <K, V> Map<K, List<V>> append(Map<K, List<V>> map, K key, V[] values) {
        Map<K, List<V>> result = new LinkedHashMap<>((Map<? extends K, ? extends List<V>>) (map != null ? map : Collections.emptyMap()));
        if (values != null) {
            result.put(key, copiedListOf(values));
        }
        return Collections.unmodifiableMap(result);
    }
}
