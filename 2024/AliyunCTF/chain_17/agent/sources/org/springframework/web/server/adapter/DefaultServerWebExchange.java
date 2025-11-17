package org.springframework.web.server.adapter;

import java.security.Principal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Hints;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.multipart.Part;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import org.springframework.web.server.i18n.LocaleContextResolver;
import org.springframework.web.server.session.WebSessionManager;
import reactor.core.publisher.Mono;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/adapter/DefaultServerWebExchange.class */
public class DefaultServerWebExchange implements ServerWebExchange {
    private static final Set<HttpMethod> SAFE_METHODS = Set.of(HttpMethod.GET, HttpMethod.HEAD);
    private static final ResolvableType FORM_DATA_TYPE = ResolvableType.forClassWithGenerics((Class<?>) MultiValueMap.class, (Class<?>[]) new Class[]{String.class, String.class});
    private static final ResolvableType MULTIPART_DATA_TYPE = ResolvableType.forClassWithGenerics((Class<?>) MultiValueMap.class, (Class<?>[]) new Class[]{String.class, Part.class});
    private static final Mono<MultiValueMap<String, String>> EMPTY_FORM_DATA = Mono.just(CollectionUtils.unmodifiableMultiValueMap(new LinkedMultiValueMap(0))).cache();
    private static final Mono<MultiValueMap<String, Part>> EMPTY_MULTIPART_DATA = Mono.just(CollectionUtils.unmodifiableMultiValueMap(new LinkedMultiValueMap(0))).cache();
    private final ServerHttpRequest request;
    private final ServerHttpResponse response;
    private final Map<String, Object> attributes;
    private final Mono<WebSession> sessionMono;
    private final LocaleContextResolver localeContextResolver;
    private final Mono<MultiValueMap<String, String>> formDataMono;
    private final Mono<MultiValueMap<String, Part>> multipartDataMono;
    private volatile boolean multipartRead;

    @Nullable
    private final ApplicationContext applicationContext;
    private volatile boolean notModified;
    private Function<String, String> urlTransformer;

    @Nullable
    private Object logId;
    private String logPrefix;

    public DefaultServerWebExchange(ServerHttpRequest request, ServerHttpResponse response, WebSessionManager sessionManager, ServerCodecConfigurer codecConfigurer, LocaleContextResolver localeContextResolver) {
        this(request, response, sessionManager, codecConfigurer, localeContextResolver, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultServerWebExchange(ServerHttpRequest request, ServerHttpResponse response, WebSessionManager sessionManager, ServerCodecConfigurer codecConfigurer, LocaleContextResolver localeContextResolver, @Nullable ApplicationContext applicationContext) {
        this.attributes = new ConcurrentHashMap();
        this.multipartRead = false;
        this.urlTransformer = url -> {
            return url;
        };
        this.logPrefix = "";
        Assert.notNull(request, "'request' is required");
        Assert.notNull(response, "'response' is required");
        Assert.notNull(sessionManager, "'sessionManager' is required");
        Assert.notNull(codecConfigurer, "'codecConfigurer' is required");
        Assert.notNull(localeContextResolver, "'localeContextResolver' is required");
        this.attributes.put(ServerWebExchange.LOG_ID_ATTRIBUTE, request.getId());
        this.request = request;
        this.response = response;
        this.sessionMono = sessionManager.getSession(this).cache();
        this.localeContextResolver = localeContextResolver;
        this.formDataMono = initFormData(request, codecConfigurer, getLogPrefix());
        this.multipartDataMono = initMultipartData(codecConfigurer, getLogPrefix());
        this.applicationContext = applicationContext;
    }

    private static Mono<MultiValueMap<String, String>> initFormData(ServerHttpRequest request, ServerCodecConfigurer configurer, String logPrefix) {
        MediaType contentType = getContentType(request);
        if (contentType == null || !contentType.isCompatibleWith(MediaType.APPLICATION_FORM_URLENCODED)) {
            return EMPTY_FORM_DATA;
        }
        HttpMessageReader<MultiValueMap<String, String>> reader = getReader(configurer, contentType, FORM_DATA_TYPE);
        if (reader == null) {
            return Mono.error(new IllegalStateException("No HttpMessageReader for " + contentType));
        }
        return reader.readMono(FORM_DATA_TYPE, request, Hints.from(Hints.LOG_PREFIX_HINT, logPrefix)).switchIfEmpty(EMPTY_FORM_DATA).cache();
    }

    private Mono<MultiValueMap<String, Part>> initMultipartData(ServerCodecConfigurer configurer, String logPrefix) {
        MediaType contentType = getContentType(this.request);
        if (contentType == null || !contentType.getType().equalsIgnoreCase("multipart")) {
            return EMPTY_MULTIPART_DATA;
        }
        HttpMessageReader<MultiValueMap<String, Part>> reader = getReader(configurer, contentType, MULTIPART_DATA_TYPE);
        if (reader == null) {
            return Mono.error(new IllegalStateException("No HttpMessageReader for " + contentType));
        }
        return reader.readMono(MULTIPART_DATA_TYPE, this.request, Hints.from(Hints.LOG_PREFIX_HINT, logPrefix)).doOnNext(ignored -> {
            this.multipartRead = true;
        }).switchIfEmpty(EMPTY_MULTIPART_DATA).cache();
    }

    @Nullable
    private static MediaType getContentType(ServerHttpRequest request) {
        MediaType contentType = null;
        try {
            contentType = request.getHeaders().getContentType();
        } catch (InvalidMediaTypeException e) {
        }
        return contentType;
    }

    @Nullable
    private static <E> HttpMessageReader<E> getReader(ServerCodecConfigurer serverCodecConfigurer, MediaType mediaType, ResolvableType resolvableType) {
        HttpMessageReader<?> httpMessageReader = null;
        for (HttpMessageReader<?> httpMessageReader2 : serverCodecConfigurer.getReaders()) {
            if (httpMessageReader2.canRead(resolvableType, mediaType)) {
                httpMessageReader = httpMessageReader2;
            }
        }
        return (HttpMessageReader<E>) httpMessageReader;
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public ServerHttpRequest getRequest() {
        return this.request;
    }

    private HttpHeaders getRequestHeaders() {
        return getRequest().getHeaders();
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public ServerHttpResponse getResponse() {
        return this.response;
    }

    private HttpHeaders getResponseHeaders() {
        return getResponse().getHeaders();
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public Mono<WebSession> getSession() {
        return this.sessionMono;
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public <T extends Principal> Mono<T> getPrincipal() {
        return Mono.empty();
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public Mono<MultiValueMap<String, String>> getFormData() {
        return this.formDataMono;
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public Mono<MultiValueMap<String, Part>> getMultipartData() {
        return this.multipartDataMono;
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public Mono<Void> cleanupMultipart() {
        return Mono.defer(() -> {
            if (this.multipartRead) {
                return getMultipartData().onErrorComplete().flatMapIterable((v0) -> {
                    return v0.values();
                }).flatMapIterable(Function.identity()).flatMap(part -> {
                    return part.delete().onErrorComplete();
                }).then();
            }
            return Mono.empty();
        });
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public LocaleContext getLocaleContext() {
        return this.localeContextResolver.resolveLocaleContext(this);
    }

    @Override // org.springframework.web.server.ServerWebExchange
    @Nullable
    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public boolean isNotModified() {
        return this.notModified;
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public boolean checkNotModified(Instant lastModified) {
        return checkNotModified(null, lastModified);
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public boolean checkNotModified(String etag) {
        return checkNotModified(etag, Instant.MIN);
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public boolean checkNotModified(@Nullable String eTag, Instant lastModified) {
        HttpStatusCode status = getResponse().getStatusCode();
        if (this.notModified || (status != null && !HttpStatus.OK.equals(status))) {
            return this.notModified;
        }
        if (validateIfMatch(eTag)) {
            updateResponseStateChanging(eTag, lastModified);
            return this.notModified;
        }
        if (validateIfUnmodifiedSince(lastModified)) {
            updateResponseStateChanging(eTag, lastModified);
            return this.notModified;
        }
        if (!validateIfNoneMatch(eTag)) {
            validateIfModifiedSince(lastModified);
        }
        updateResponseIdempotent(eTag, lastModified);
        return this.notModified;
    }

    private boolean validateIfMatch(@Nullable String eTag) {
        try {
            if (SAFE_METHODS.contains(getRequest().getMethod()) || CollectionUtils.isEmpty(getRequestHeaders().get(HttpHeaders.IF_MATCH))) {
                return false;
            }
            this.notModified = matchRequestedETags(getRequestHeaders().getIfMatch(), eTag, false);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean matchRequestedETags(List<String> requestedETags, @Nullable String eTag, boolean weakCompare) {
        String eTag2 = padEtagIfNecessary(eTag);
        for (String clientEtag : requestedETags) {
            if ("*".equals(clientEtag) && StringUtils.hasLength(eTag2) && !SAFE_METHODS.contains(getRequest().getMethod())) {
                return false;
            }
            if (weakCompare) {
                if (eTagWeakMatch(eTag2, clientEtag)) {
                    return false;
                }
            } else if (eTagStrongMatch(eTag2, clientEtag)) {
                return false;
            }
        }
        return true;
    }

    @Nullable
    private String padEtagIfNecessary(@Nullable String etag) {
        if (!StringUtils.hasLength(etag)) {
            return etag;
        }
        if ((etag.startsWith("\"") || etag.startsWith("W/\"")) && etag.endsWith("\"")) {
            return etag;
        }
        return "\"" + etag + "\"";
    }

    private boolean eTagStrongMatch(@Nullable String first, @Nullable String second) {
        if (!StringUtils.hasLength(first) || first.startsWith("W/")) {
            return false;
        }
        return first.equals(second);
    }

    private boolean eTagWeakMatch(@Nullable String first, @Nullable String second) {
        if (!StringUtils.hasLength(first) || !StringUtils.hasLength(second)) {
            return false;
        }
        if (first.startsWith("W/")) {
            first = first.substring(2);
        }
        if (second.startsWith("W/")) {
            second = second.substring(2);
        }
        return first.equals(second);
    }

    private void updateResponseStateChanging(@Nullable String eTag, Instant lastModified) {
        if (this.notModified) {
            getResponse().setStatusCode(HttpStatus.PRECONDITION_FAILED);
        } else {
            addCachingResponseHeaders(eTag, lastModified);
        }
    }

    private boolean validateIfNoneMatch(@Nullable String eTag) {
        try {
            if (CollectionUtils.isEmpty(getRequestHeaders().get(HttpHeaders.IF_NONE_MATCH))) {
                return false;
            }
            this.notModified = !matchRequestedETags(getRequestHeaders().getIfNoneMatch(), eTag, true);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private void updateResponseIdempotent(@Nullable String eTag, Instant lastModified) {
        boolean isSafeMethod = SAFE_METHODS.contains(getRequest().getMethod());
        if (this.notModified) {
            getResponse().setStatusCode(isSafeMethod ? HttpStatus.NOT_MODIFIED : HttpStatus.PRECONDITION_FAILED);
        }
        addCachingResponseHeaders(eTag, lastModified);
    }

    private void addCachingResponseHeaders(@Nullable String eTag, Instant lastModified) {
        if (SAFE_METHODS.contains(getRequest().getMethod())) {
            if (lastModified.isAfter(Instant.EPOCH) && getResponseHeaders().getLastModified() == -1) {
                getResponseHeaders().setLastModified(lastModified.toEpochMilli());
            }
            if (StringUtils.hasLength(eTag) && getResponseHeaders().getETag() == null) {
                getResponseHeaders().setETag(padEtagIfNecessary(eTag));
            }
        }
    }

    private boolean validateIfUnmodifiedSince(Instant lastModified) {
        if (lastModified.isBefore(Instant.EPOCH)) {
            return false;
        }
        long ifUnmodifiedSince = getRequestHeaders().getIfUnmodifiedSince();
        if (ifUnmodifiedSince == -1) {
            return false;
        }
        Instant sinceInstant = Instant.ofEpochMilli(ifUnmodifiedSince);
        this.notModified = sinceInstant.isBefore(lastModified.truncatedTo(ChronoUnit.SECONDS));
        return true;
    }

    private void validateIfModifiedSince(Instant lastModified) {
        if (lastModified.isBefore(Instant.EPOCH)) {
            return;
        }
        long ifModifiedSince = getRequestHeaders().getIfModifiedSince();
        if (ifModifiedSince != -1) {
            this.notModified = ChronoUnit.SECONDS.between(lastModified, Instant.ofEpochMilli(ifModifiedSince)) >= 0;
        }
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public String transformUrl(String url) {
        return this.urlTransformer.apply(url);
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public void addUrlTransformer(Function<String, String> transformer) {
        Assert.notNull(transformer, "'encoder' must not be null");
        this.urlTransformer = this.urlTransformer.andThen(transformer);
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public String getLogPrefix() {
        Object value = getAttribute(LOG_ID_ATTRIBUTE);
        if (this.logId != value) {
            this.logId = value;
            this.logPrefix = value != null ? "[" + value + "] " : "";
        }
        return this.logPrefix;
    }
}
