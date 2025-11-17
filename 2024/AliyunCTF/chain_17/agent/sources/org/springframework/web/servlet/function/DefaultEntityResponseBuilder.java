package org.springframework.web.servlet.function;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ReactiveAdapter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.function.EntityResponse;
import org.springframework.web.servlet.function.ServerResponse;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/DefaultEntityResponseBuilder.class */
public final class DefaultEntityResponseBuilder<T> implements EntityResponse.Builder<T> {
    private static final Type RESOURCE_REGION_LIST_TYPE = new ParameterizedTypeReference<List<ResourceRegion>>() { // from class: org.springframework.web.servlet.function.DefaultEntityResponseBuilder.1
    }.getType();
    private final T entity;
    private final Type entityType;
    private HttpStatusCode status = HttpStatus.OK;
    private final HttpHeaders headers = new HttpHeaders();
    private final MultiValueMap<String, Cookie> cookies = new LinkedMultiValueMap();

    private DefaultEntityResponseBuilder(T entity, @Nullable Type entityType) {
        this.entity = entity;
        this.entityType = entityType != null ? entityType : entity.getClass();
    }

    @Override // org.springframework.web.servlet.function.EntityResponse.Builder
    public EntityResponse.Builder<T> status(HttpStatusCode status) {
        Assert.notNull(status, "HttpStatusCode must not be null");
        this.status = status;
        return this;
    }

    @Override // org.springframework.web.servlet.function.EntityResponse.Builder
    public EntityResponse.Builder<T> status(int status) {
        return status(HttpStatusCode.valueOf(status));
    }

    @Override // org.springframework.web.servlet.function.EntityResponse.Builder
    public EntityResponse.Builder<T> cookie(Cookie cookie) {
        Assert.notNull(cookie, "Cookie must not be null");
        this.cookies.add(cookie.getName(), cookie);
        return this;
    }

    @Override // org.springframework.web.servlet.function.EntityResponse.Builder
    public EntityResponse.Builder<T> cookies(Consumer<MultiValueMap<String, Cookie>> cookiesConsumer) {
        Assert.notNull(cookiesConsumer, "cookiesConsumer must not be null");
        cookiesConsumer.accept(this.cookies);
        return this;
    }

    @Override // org.springframework.web.servlet.function.EntityResponse.Builder
    public EntityResponse.Builder<T> header(String headerName, String... headerValues) {
        Assert.notNull(headerName, "headerName must not be null");
        for (String headerValue : headerValues) {
            this.headers.add(headerName, headerValue);
        }
        return this;
    }

    @Override // org.springframework.web.servlet.function.EntityResponse.Builder
    public EntityResponse.Builder<T> headers(Consumer<HttpHeaders> headersConsumer) {
        Assert.notNull(headersConsumer, "headersConsumer must not be null");
        headersConsumer.accept(this.headers);
        return this;
    }

    @Override // org.springframework.web.servlet.function.EntityResponse.Builder
    public EntityResponse.Builder<T> allow(HttpMethod... allowedMethods) {
        Assert.notNull(allowedMethods, "allowedMethods must not be null");
        this.headers.setAllow(new LinkedHashSet(Arrays.asList(allowedMethods)));
        return this;
    }

    @Override // org.springframework.web.servlet.function.EntityResponse.Builder
    public EntityResponse.Builder<T> allow(Set<HttpMethod> allowedMethods) {
        Assert.notNull(allowedMethods, "allowedMethods must not be null");
        this.headers.setAllow(allowedMethods);
        return this;
    }

    @Override // org.springframework.web.servlet.function.EntityResponse.Builder
    public EntityResponse.Builder<T> contentLength(long contentLength) {
        this.headers.setContentLength(contentLength);
        return this;
    }

    @Override // org.springframework.web.servlet.function.EntityResponse.Builder
    public EntityResponse.Builder<T> contentType(MediaType contentType) {
        Assert.notNull(contentType, "contentType must not be null");
        this.headers.setContentType(contentType);
        return this;
    }

    @Override // org.springframework.web.servlet.function.EntityResponse.Builder
    public EntityResponse.Builder<T> eTag(String etag) {
        if (!etag.startsWith("\"") && !etag.startsWith("W/\"")) {
            etag = "\"" + etag;
        }
        if (!etag.endsWith("\"")) {
            etag = etag + "\"";
        }
        this.headers.setETag(etag);
        return this;
    }

    @Override // org.springframework.web.servlet.function.EntityResponse.Builder
    public EntityResponse.Builder<T> lastModified(ZonedDateTime lastModified) {
        Assert.notNull(lastModified, "lastModified must not be null");
        this.headers.setLastModified(lastModified);
        return this;
    }

    @Override // org.springframework.web.servlet.function.EntityResponse.Builder
    public EntityResponse.Builder<T> lastModified(Instant lastModified) {
        Assert.notNull(lastModified, "lastModified must not be null");
        this.headers.setLastModified(lastModified);
        return this;
    }

    @Override // org.springframework.web.servlet.function.EntityResponse.Builder
    public EntityResponse.Builder<T> location(URI location) {
        Assert.notNull(location, "location must not be null");
        this.headers.setLocation(location);
        return this;
    }

    @Override // org.springframework.web.servlet.function.EntityResponse.Builder
    public EntityResponse.Builder<T> cacheControl(CacheControl cacheControl) {
        Assert.notNull(cacheControl, "cacheControl must not be null");
        this.headers.setCacheControl(cacheControl);
        return this;
    }

    @Override // org.springframework.web.servlet.function.EntityResponse.Builder
    public EntityResponse.Builder<T> varyBy(String... requestHeaders) {
        this.headers.setVary(Arrays.asList(requestHeaders));
        return this;
    }

    @Override // org.springframework.web.servlet.function.EntityResponse.Builder
    public EntityResponse<T> build() {
        ReactiveAdapter adapter;
        T t = this.entity;
        if (t instanceof CompletionStage) {
            CompletionStage completionStage = (CompletionStage) t;
            return new CompletionStageEntityResponse(this.status, this.headers, this.cookies, completionStage, this.entityType);
        }
        if (DefaultAsyncServerResponse.reactiveStreamsPresent && (adapter = ReactiveAdapterRegistry.getSharedInstance().getAdapter(this.entity.getClass())) != null) {
            Publisher<T> publisher = adapter.toPublisher(this.entity);
            return new PublisherEntityResponse(this.status, this.headers, this.cookies, publisher, this.entityType);
        }
        return new DefaultEntityResponse(this.status, this.headers, this.cookies, this.entity, this.entityType);
    }

    public static <T> EntityResponse.Builder<T> fromObject(T t) {
        return new DefaultEntityResponseBuilder(t, null);
    }

    public static <T> EntityResponse.Builder<T> fromObject(T t, ParameterizedTypeReference<?> bodyType) {
        return new DefaultEntityResponseBuilder(t, bodyType.getType());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/DefaultEntityResponseBuilder$DefaultEntityResponse.class */
    public static class DefaultEntityResponse<T> extends AbstractServerResponse implements EntityResponse<T> {
        private final T entity;
        private final Type entityType;

        public DefaultEntityResponse(HttpStatusCode statusCode, HttpHeaders headers, MultiValueMap<String, Cookie> cookies, T entity, Type entityType) {
            super(statusCode, headers, cookies);
            this.entity = entity;
            this.entityType = entityType;
        }

        @Override // org.springframework.web.servlet.function.EntityResponse
        public T entity() {
            return this.entity;
        }

        @Override // org.springframework.web.servlet.function.AbstractServerResponse
        protected ModelAndView writeToInternal(HttpServletRequest servletRequest, HttpServletResponse servletResponse, ServerResponse.Context context) throws ServletException, IOException {
            writeEntityWithMessageConverters(this.entity, servletRequest, servletResponse, context);
            return null;
        }

        /* JADX WARN: Multi-variable type inference failed */
        protected void writeEntityWithMessageConverters(Object entity, HttpServletRequest request, HttpServletResponse response, ServerResponse.Context context) throws ServletException, IOException {
            ServletServerHttpResponse serverResponse = new ServletServerHttpResponse(response);
            MediaType contentType = getContentType(response);
            Class<?> entityClass = entity.getClass();
            Type entityType = this.entityType;
            if (entityClass != InputStreamResource.class && Resource.class.isAssignableFrom(entityClass)) {
                serverResponse.getHeaders().set(HttpHeaders.ACCEPT_RANGES, "bytes");
                String rangeHeader = request.getHeader(HttpHeaders.RANGE);
                if (rangeHeader != null) {
                    Resource resource = (Resource) entity;
                    try {
                        List<HttpRange> httpRanges = HttpRange.parseRanges(rangeHeader);
                        serverResponse.getServletResponse().setStatus(HttpStatus.PARTIAL_CONTENT.value());
                        entity = HttpRange.toResourceRegions(httpRanges, resource);
                        entityClass = entity.getClass();
                        entityType = DefaultEntityResponseBuilder.RESOURCE_REGION_LIST_TYPE;
                    } catch (IllegalArgumentException e) {
                        serverResponse.getHeaders().set(HttpHeaders.CONTENT_RANGE, "bytes */" + resource.contentLength());
                        serverResponse.getServletResponse().setStatus(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.value());
                    }
                }
            }
            for (HttpMessageConverter<?> messageConverter : context.messageConverters()) {
                if (messageConverter instanceof GenericHttpMessageConverter) {
                    GenericHttpMessageConverter genericHttpMessageConverter = (GenericHttpMessageConverter) messageConverter;
                    if (genericHttpMessageConverter.canWrite(entityType, entityClass, contentType)) {
                        genericHttpMessageConverter.write(entity, entityType, contentType, serverResponse);
                        return;
                    }
                }
                if (messageConverter.canWrite(entityClass, contentType)) {
                    messageConverter.write(entity, contentType, serverResponse);
                    return;
                }
            }
            List<MediaType> producibleMediaTypes = producibleMediaTypes(context.messageConverters(), entityClass);
            throw new HttpMediaTypeNotAcceptableException(producibleMediaTypes);
        }

        @Nullable
        private static MediaType getContentType(HttpServletResponse response) {
            try {
                return MediaType.parseMediaType(response.getContentType()).removeQualityValue();
            } catch (InvalidMediaTypeException e) {
                return null;
            }
        }

        protected void tryWriteEntityWithMessageConverters(Object entity, HttpServletRequest request, HttpServletResponse response, ServerResponse.Context context) throws ServletException, IOException {
            try {
                writeEntityWithMessageConverters(entity, request, response, context);
            } catch (ServletException | IOException ex) {
                handleError(ex, request, response, context);
            }
        }

        private static List<MediaType> producibleMediaTypes(List<HttpMessageConverter<?>> messageConverters, Class<?> entityClass) {
            return messageConverters.stream().filter(messageConverter -> {
                return messageConverter.canWrite(entityClass, null);
            }).flatMap(messageConverter2 -> {
                return messageConverter2.getSupportedMediaTypes(entityClass).stream();
            }).toList();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/DefaultEntityResponseBuilder$CompletionStageEntityResponse.class */
    private static class CompletionStageEntityResponse<T> extends DefaultEntityResponse<CompletionStage<T>> {
        public CompletionStageEntityResponse(HttpStatusCode statusCode, HttpHeaders headers, MultiValueMap<String, Cookie> cookies, CompletionStage<T> entity, Type entityType) {
            super(statusCode, headers, cookies, entity, entityType);
        }

        @Override // org.springframework.web.servlet.function.DefaultEntityResponseBuilder.DefaultEntityResponse, org.springframework.web.servlet.function.AbstractServerResponse
        protected ModelAndView writeToInternal(HttpServletRequest servletRequest, HttpServletResponse servletResponse, ServerResponse.Context context) throws ServletException, IOException {
            DeferredResult<ServerResponse> deferredResult = createDeferredResult(servletRequest, servletResponse, context);
            DefaultAsyncServerResponse.writeAsync(servletRequest, servletResponse, deferredResult);
            return null;
        }

        private DeferredResult<ServerResponse> createDeferredResult(HttpServletRequest request, HttpServletResponse response, ServerResponse.Context context) {
            DeferredResult<ServerResponse> result = new DeferredResult<>();
            ((CompletionStage) entity()).whenComplete((value, ex) -> {
                if (ex != null) {
                    if ((ex instanceof CompletionException) && ex.getCause() != null) {
                        ex = ex.getCause();
                    }
                    ServerResponse errorResponse = errorResponse(ex, request);
                    if (errorResponse != null) {
                        result.setResult(errorResponse);
                        return;
                    } else {
                        result.setErrorResult(ex);
                        return;
                    }
                }
                try {
                    tryWriteEntityWithMessageConverters(value, request, response, context);
                    result.setResult(null);
                } catch (ServletException | IOException writeException) {
                    result.setErrorResult(writeException);
                }
            });
            return result;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/DefaultEntityResponseBuilder$PublisherEntityResponse.class */
    private static class PublisherEntityResponse<T> extends DefaultEntityResponse<Publisher<T>> {
        public PublisherEntityResponse(HttpStatusCode statusCode, HttpHeaders headers, MultiValueMap<String, Cookie> cookies, Publisher<T> entity, Type entityType) {
            super(statusCode, headers, cookies, entity, entityType);
        }

        @Override // org.springframework.web.servlet.function.DefaultEntityResponseBuilder.DefaultEntityResponse, org.springframework.web.servlet.function.AbstractServerResponse
        protected ModelAndView writeToInternal(HttpServletRequest servletRequest, HttpServletResponse servletResponse, ServerResponse.Context context) throws ServletException, IOException {
            DeferredResult<?> deferredResult = new DeferredResult<>();
            DefaultAsyncServerResponse.writeAsync(servletRequest, servletResponse, deferredResult);
            ((Publisher) entity()).subscribe(new DeferredResultSubscriber(servletRequest, servletResponse, context, deferredResult));
            return null;
        }

        /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/DefaultEntityResponseBuilder$PublisherEntityResponse$DeferredResultSubscriber.class */
        private class DeferredResultSubscriber implements Subscriber<T> {
            private final HttpServletRequest servletRequest;
            private final HttpServletResponse servletResponse;
            private final ServerResponse.Context context;
            private final DeferredResult<?> deferredResult;

            @Nullable
            private Subscription subscription;

            public DeferredResultSubscriber(HttpServletRequest servletRequest, HttpServletResponse servletResponse, ServerResponse.Context context, DeferredResult<?> deferredResult) {
                this.servletRequest = servletRequest;
                this.servletResponse = new NoContentLengthResponseWrapper(servletResponse);
                this.context = context;
                this.deferredResult = deferredResult;
            }

            public void onSubscribe(Subscription s) {
                if (this.subscription == null) {
                    this.subscription = s;
                    this.subscription.request(1L);
                } else {
                    s.cancel();
                }
            }

            public void onNext(T t) {
                Assert.state(this.subscription != null, "No subscription");
                try {
                    PublisherEntityResponse.this.tryWriteEntityWithMessageConverters(t, this.servletRequest, this.servletResponse, this.context);
                    this.servletResponse.getOutputStream().flush();
                    this.subscription.request(1L);
                } catch (ServletException | IOException ex) {
                    this.subscription.cancel();
                    this.deferredResult.setErrorResult(ex);
                }
            }

            public void onError(Throwable t) {
                try {
                    PublisherEntityResponse.this.handleError(t, this.servletRequest, this.servletResponse, this.context);
                } catch (ServletException | IOException handlingThrowable) {
                    this.deferredResult.setErrorResult(handlingThrowable);
                }
            }

            public void onComplete() {
                try {
                    this.servletResponse.getOutputStream().flush();
                    this.deferredResult.setResult(null);
                } catch (IOException ex) {
                    this.deferredResult.setErrorResult(ex);
                }
            }
        }

        /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/DefaultEntityResponseBuilder$PublisherEntityResponse$NoContentLengthResponseWrapper.class */
        private static class NoContentLengthResponseWrapper extends HttpServletResponseWrapper {
            public NoContentLengthResponseWrapper(HttpServletResponse response) {
                super(response);
            }

            @Override // jakarta.servlet.http.HttpServletResponseWrapper, jakarta.servlet.http.HttpServletResponse
            public void addIntHeader(String name, int value) {
                if (!HttpHeaders.CONTENT_LENGTH.equals(name)) {
                    super.addIntHeader(name, value);
                }
            }

            @Override // jakarta.servlet.http.HttpServletResponseWrapper, jakarta.servlet.http.HttpServletResponse
            public void addHeader(String name, String value) {
                if (!HttpHeaders.CONTENT_LENGTH.equals(name)) {
                    super.addHeader(name, value);
                }
            }

            @Override // jakarta.servlet.ServletResponseWrapper, jakarta.servlet.ServletResponse
            public void setContentLength(int len) {
            }

            @Override // jakarta.servlet.ServletResponseWrapper, jakarta.servlet.ServletResponse
            public void setContentLengthLong(long len) {
            }
        }
    }
}
