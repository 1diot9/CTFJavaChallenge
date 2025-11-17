package org.springframework.web.service.invoker;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.runtime.ObjectMethods;
import java.time.Duration;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import org.reactivestreams.Publisher;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.KotlinDetector;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ReactiveAdapter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.invoker.HttpRequestValues;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/service/invoker/HttpServiceMethod.class */
final class HttpServiceMethod {
    private static final boolean REACTOR_PRESENT = ClassUtils.isPresent("reactor.core.publisher.Mono", HttpServiceMethod.class.getClassLoader());
    private final Method method;
    private final MethodParameter[] parameters;
    private final List<HttpServiceArgumentResolver> argumentResolvers;
    private final HttpRequestValuesInitializer requestValuesInitializer;
    private final ResponseFunction responseFunction;

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/service/invoker/HttpServiceMethod$ResponseFunction.class */
    private interface ResponseFunction {
        @Nullable
        Object execute(HttpRequestValues requestValues);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HttpServiceMethod(Method method, Class<?> containingClass, List<HttpServiceArgumentResolver> argumentResolvers, HttpExchangeAdapter adapter, @Nullable StringValueResolver embeddedValueResolver) {
        ResponseFunction create;
        this.method = method;
        this.parameters = initMethodParameters(method);
        this.argumentResolvers = argumentResolvers;
        boolean isReactorAdapter = REACTOR_PRESENT && (adapter instanceof ReactorHttpExchangeAdapter);
        this.requestValuesInitializer = HttpRequestValuesInitializer.create(method, containingClass, embeddedValueResolver, isReactorAdapter ? ReactiveHttpRequestValues::builder : HttpRequestValues::builder);
        if (isReactorAdapter) {
            create = ReactorExchangeResponseFunction.create((ReactorHttpExchangeAdapter) adapter, method);
        } else {
            create = ExchangeResponseFunction.create(adapter, method);
        }
        this.responseFunction = create;
    }

    private static MethodParameter[] initMethodParameters(Method method) {
        int count = method.getParameterCount();
        if (count == 0) {
            return new MethodParameter[0];
        }
        if (KotlinDetector.isSuspendingFunction(method)) {
            count--;
        }
        DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();
        MethodParameter[] parameters = new MethodParameter[count];
        for (int i = 0; i < count; i++) {
            parameters[i] = new SynthesizingMethodParameter(method, i);
            parameters[i].initParameterNameDiscovery(nameDiscoverer);
        }
        return parameters;
    }

    public Method getMethod() {
        return this.method;
    }

    @Nullable
    public Object invoke(Object[] arguments) {
        HttpRequestValues.Builder requestValues = this.requestValuesInitializer.initializeRequestValuesBuilder();
        applyArguments(requestValues, arguments);
        return this.responseFunction.execute(requestValues.build());
    }

    private void applyArguments(HttpRequestValues.Builder requestValues, Object[] arguments) {
        Assert.isTrue(arguments.length == this.parameters.length, "Method argument mismatch");
        for (int i = 0; i < arguments.length; i++) {
            Object value = arguments[i];
            boolean resolved = false;
            Iterator<HttpServiceArgumentResolver> it = this.argumentResolvers.iterator();
            while (true) {
                if (it.hasNext()) {
                    HttpServiceArgumentResolver resolver = it.next();
                    if (resolver.resolve(value, this.parameters[i], requestValues)) {
                        resolved = true;
                        break;
                    }
                }
            }
            int index = i;
            Assert.state(resolved, (Supplier<String>) () -> {
                return "Could not resolve parameter [" + this.parameters[index].getParameterIndex() + "] in " + this.parameters[index].getExecutable().toGenericString() + (StringUtils.hasText("No suitable resolver") ? ": No suitable resolver" : "");
            });
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/service/invoker/HttpServiceMethod$HttpRequestValuesInitializer.class */
    private static final class HttpRequestValuesInitializer extends Record {

        @Nullable
        private final HttpMethod httpMethod;

        @Nullable
        private final String url;

        @Nullable
        private final MediaType contentType;

        @Nullable
        private final List<MediaType> acceptMediaTypes;
        private final Supplier<HttpRequestValues.Builder> requestValuesSupplier;

        private HttpRequestValuesInitializer(@Nullable HttpMethod httpMethod, @Nullable String url, @Nullable MediaType contentType, @Nullable List<MediaType> acceptMediaTypes, Supplier<HttpRequestValues.Builder> requestValuesSupplier) {
            this.httpMethod = httpMethod;
            this.url = url;
            this.contentType = contentType;
            this.acceptMediaTypes = acceptMediaTypes;
            this.requestValuesSupplier = requestValuesSupplier;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, HttpRequestValuesInitializer.class), HttpRequestValuesInitializer.class, "httpMethod;url;contentType;acceptMediaTypes;requestValuesSupplier", "FIELD:Lorg/springframework/web/service/invoker/HttpServiceMethod$HttpRequestValuesInitializer;->httpMethod:Lorg/springframework/http/HttpMethod;", "FIELD:Lorg/springframework/web/service/invoker/HttpServiceMethod$HttpRequestValuesInitializer;->url:Ljava/lang/String;", "FIELD:Lorg/springframework/web/service/invoker/HttpServiceMethod$HttpRequestValuesInitializer;->contentType:Lorg/springframework/http/MediaType;", "FIELD:Lorg/springframework/web/service/invoker/HttpServiceMethod$HttpRequestValuesInitializer;->acceptMediaTypes:Ljava/util/List;", "FIELD:Lorg/springframework/web/service/invoker/HttpServiceMethod$HttpRequestValuesInitializer;->requestValuesSupplier:Ljava/util/function/Supplier;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, HttpRequestValuesInitializer.class), HttpRequestValuesInitializer.class, "httpMethod;url;contentType;acceptMediaTypes;requestValuesSupplier", "FIELD:Lorg/springframework/web/service/invoker/HttpServiceMethod$HttpRequestValuesInitializer;->httpMethod:Lorg/springframework/http/HttpMethod;", "FIELD:Lorg/springframework/web/service/invoker/HttpServiceMethod$HttpRequestValuesInitializer;->url:Ljava/lang/String;", "FIELD:Lorg/springframework/web/service/invoker/HttpServiceMethod$HttpRequestValuesInitializer;->contentType:Lorg/springframework/http/MediaType;", "FIELD:Lorg/springframework/web/service/invoker/HttpServiceMethod$HttpRequestValuesInitializer;->acceptMediaTypes:Ljava/util/List;", "FIELD:Lorg/springframework/web/service/invoker/HttpServiceMethod$HttpRequestValuesInitializer;->requestValuesSupplier:Ljava/util/function/Supplier;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, HttpRequestValuesInitializer.class, Object.class), HttpRequestValuesInitializer.class, "httpMethod;url;contentType;acceptMediaTypes;requestValuesSupplier", "FIELD:Lorg/springframework/web/service/invoker/HttpServiceMethod$HttpRequestValuesInitializer;->httpMethod:Lorg/springframework/http/HttpMethod;", "FIELD:Lorg/springframework/web/service/invoker/HttpServiceMethod$HttpRequestValuesInitializer;->url:Ljava/lang/String;", "FIELD:Lorg/springframework/web/service/invoker/HttpServiceMethod$HttpRequestValuesInitializer;->contentType:Lorg/springframework/http/MediaType;", "FIELD:Lorg/springframework/web/service/invoker/HttpServiceMethod$HttpRequestValuesInitializer;->acceptMediaTypes:Ljava/util/List;", "FIELD:Lorg/springframework/web/service/invoker/HttpServiceMethod$HttpRequestValuesInitializer;->requestValuesSupplier:Ljava/util/function/Supplier;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        @Nullable
        public HttpMethod httpMethod() {
            return this.httpMethod;
        }

        @Nullable
        public String url() {
            return this.url;
        }

        @Nullable
        public MediaType contentType() {
            return this.contentType;
        }

        @Nullable
        public List<MediaType> acceptMediaTypes() {
            return this.acceptMediaTypes;
        }

        public Supplier<HttpRequestValues.Builder> requestValuesSupplier() {
            return this.requestValuesSupplier;
        }

        public HttpRequestValues.Builder initializeRequestValuesBuilder() {
            HttpRequestValues.Builder requestValues = this.requestValuesSupplier.get();
            if (this.httpMethod != null) {
                requestValues.setHttpMethod(this.httpMethod);
            }
            if (this.url != null) {
                requestValues.setUriTemplate(this.url);
            }
            if (this.contentType != null) {
                requestValues.setContentType(this.contentType);
            }
            if (this.acceptMediaTypes != null) {
                requestValues.setAccept(this.acceptMediaTypes);
            }
            return requestValues;
        }

        public static HttpRequestValuesInitializer create(Method method, Class<?> containingClass, @Nullable StringValueResolver embeddedValueResolver, Supplier<HttpRequestValues.Builder> requestValuesSupplier) {
            HttpExchange annot1 = (HttpExchange) AnnotatedElementUtils.findMergedAnnotation(containingClass, HttpExchange.class);
            HttpExchange annot2 = (HttpExchange) AnnotatedElementUtils.findMergedAnnotation(method, HttpExchange.class);
            Assert.notNull(annot2, "Expected HttpRequest annotation");
            HttpMethod httpMethod = initHttpMethod(annot1, annot2);
            String url = initUrl(annot1, annot2, embeddedValueResolver);
            MediaType contentType = initContentType(annot1, annot2);
            List<MediaType> acceptableMediaTypes = initAccept(annot1, annot2);
            return new HttpRequestValuesInitializer(httpMethod, url, contentType, acceptableMediaTypes, requestValuesSupplier);
        }

        @Nullable
        private static HttpMethod initHttpMethod(@Nullable HttpExchange typeAnnot, HttpExchange annot) {
            String value1 = typeAnnot != null ? typeAnnot.method() : null;
            String value2 = annot.method();
            if (StringUtils.hasText(value2)) {
                return HttpMethod.valueOf(value2);
            }
            if (StringUtils.hasText(value1)) {
                return HttpMethod.valueOf(value1);
            }
            return null;
        }

        @Nullable
        private static String initUrl(@Nullable HttpExchange typeAnnot, HttpExchange annot, @Nullable StringValueResolver embeddedValueResolver) {
            String url1 = typeAnnot != null ? typeAnnot.url() : null;
            String url2 = annot.url();
            if (embeddedValueResolver != null) {
                url1 = url1 != null ? embeddedValueResolver.resolveStringValue(url1) : null;
                url2 = embeddedValueResolver.resolveStringValue(url2);
            }
            boolean hasUrl1 = StringUtils.hasText(url1);
            boolean hasUrl2 = StringUtils.hasText(url2);
            if (hasUrl1 && hasUrl2) {
                return url1 + ((url1.endsWith("/") || url2.startsWith("/")) ? "" : "/") + url2;
            }
            if (hasUrl1 || hasUrl2) {
                return hasUrl2 ? url2 : url1;
            }
            return null;
        }

        @Nullable
        private static MediaType initContentType(@Nullable HttpExchange typeAnnot, HttpExchange annot) {
            String value1 = typeAnnot != null ? typeAnnot.contentType() : null;
            String value2 = annot.contentType();
            if (StringUtils.hasText(value2)) {
                return MediaType.parseMediaType(value2);
            }
            if (StringUtils.hasText(value1)) {
                return MediaType.parseMediaType(value1);
            }
            return null;
        }

        @Nullable
        private static List<MediaType> initAccept(@Nullable HttpExchange typeAnnot, HttpExchange annot) {
            String[] value1 = typeAnnot != null ? typeAnnot.accept() : null;
            String[] value2 = annot.accept();
            if (!ObjectUtils.isEmpty((Object[]) value2)) {
                return MediaType.parseMediaTypes((List<String>) Arrays.asList(value2));
            }
            if (!ObjectUtils.isEmpty((Object[]) value1)) {
                return MediaType.parseMediaTypes((List<String>) Arrays.asList(value1));
            }
            return null;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/service/invoker/HttpServiceMethod$ExchangeResponseFunction.class */
    private static final class ExchangeResponseFunction extends Record implements ResponseFunction {
        private final Function<HttpRequestValues, Object> responseFunction;

        private ExchangeResponseFunction(Function<HttpRequestValues, Object> responseFunction) {
            this.responseFunction = responseFunction;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, ExchangeResponseFunction.class), ExchangeResponseFunction.class, "responseFunction", "FIELD:Lorg/springframework/web/service/invoker/HttpServiceMethod$ExchangeResponseFunction;->responseFunction:Ljava/util/function/Function;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, ExchangeResponseFunction.class), ExchangeResponseFunction.class, "responseFunction", "FIELD:Lorg/springframework/web/service/invoker/HttpServiceMethod$ExchangeResponseFunction;->responseFunction:Ljava/util/function/Function;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, ExchangeResponseFunction.class, Object.class), ExchangeResponseFunction.class, "responseFunction", "FIELD:Lorg/springframework/web/service/invoker/HttpServiceMethod$ExchangeResponseFunction;->responseFunction:Ljava/util/function/Function;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public Function<HttpRequestValues, Object> responseFunction() {
            return this.responseFunction;
        }

        @Override // org.springframework.web.service.invoker.HttpServiceMethod.ResponseFunction
        public Object execute(HttpRequestValues requestValues) {
            return this.responseFunction.apply(requestValues);
        }

        public static ResponseFunction create(HttpExchangeAdapter client, Method method) {
            Function<HttpRequestValues, Object> responseFunction;
            if (KotlinDetector.isSuspendingFunction(method)) {
                throw new IllegalStateException("Kotlin Coroutines are only supported with reactive implementations");
            }
            MethodParameter param = new MethodParameter(method, -1).nestedIfOptional();
            Class<?> paramType = param.getNestedParameterType();
            if (paramType.equals(Void.TYPE) || paramType.equals(Void.class)) {
                responseFunction = requestValues -> {
                    client.exchange(requestValues);
                    return null;
                };
            } else if (paramType.equals(HttpHeaders.class)) {
                responseFunction = request -> {
                    return asOptionalIfNecessary(client.exchangeForHeaders(request), param);
                };
            } else if (paramType.equals(ResponseEntity.class)) {
                MethodParameter bodyParam = param.nested();
                if (bodyParam.getNestedParameterType().equals(Void.class)) {
                    responseFunction = request2 -> {
                        return asOptionalIfNecessary(client.exchangeForBodilessEntity(request2), param);
                    };
                } else {
                    ParameterizedTypeReference<?> bodyTypeRef = ParameterizedTypeReference.forType(bodyParam.getNestedGenericParameterType());
                    responseFunction = request3 -> {
                        return asOptionalIfNecessary(client.exchangeForEntity(request3, bodyTypeRef), param);
                    };
                }
            } else {
                ParameterizedTypeReference<?> bodyTypeRef2 = ParameterizedTypeReference.forType(param.getNestedGenericParameterType());
                responseFunction = request4 -> {
                    return asOptionalIfNecessary(client.exchangeForBody(request4, bodyTypeRef2), param);
                };
            }
            return new ExchangeResponseFunction(responseFunction);
        }

        /* JADX INFO: Access modifiers changed from: private */
        @Nullable
        public static Object asOptionalIfNecessary(@Nullable Object response, MethodParameter param) {
            return param.getParameterType().equals(Optional.class) ? Optional.ofNullable(response) : response;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/service/invoker/HttpServiceMethod$ReactorExchangeResponseFunction.class */
    private static final class ReactorExchangeResponseFunction extends Record implements ResponseFunction {
        private final Function<HttpRequestValues, Publisher<?>> responseFunction;

        @Nullable
        private final ReactiveAdapter returnTypeAdapter;
        private final boolean blockForOptional;

        @Nullable
        private final Duration blockTimeout;

        private ReactorExchangeResponseFunction(Function<HttpRequestValues, Publisher<?>> responseFunction, @Nullable ReactiveAdapter returnTypeAdapter, boolean blockForOptional, @Nullable Duration blockTimeout) {
            this.responseFunction = responseFunction;
            this.returnTypeAdapter = returnTypeAdapter;
            this.blockForOptional = blockForOptional;
            this.blockTimeout = blockTimeout;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, ReactorExchangeResponseFunction.class), ReactorExchangeResponseFunction.class, "responseFunction;returnTypeAdapter;blockForOptional;blockTimeout", "FIELD:Lorg/springframework/web/service/invoker/HttpServiceMethod$ReactorExchangeResponseFunction;->responseFunction:Ljava/util/function/Function;", "FIELD:Lorg/springframework/web/service/invoker/HttpServiceMethod$ReactorExchangeResponseFunction;->returnTypeAdapter:Lorg/springframework/core/ReactiveAdapter;", "FIELD:Lorg/springframework/web/service/invoker/HttpServiceMethod$ReactorExchangeResponseFunction;->blockForOptional:Z", "FIELD:Lorg/springframework/web/service/invoker/HttpServiceMethod$ReactorExchangeResponseFunction;->blockTimeout:Ljava/time/Duration;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, ReactorExchangeResponseFunction.class), ReactorExchangeResponseFunction.class, "responseFunction;returnTypeAdapter;blockForOptional;blockTimeout", "FIELD:Lorg/springframework/web/service/invoker/HttpServiceMethod$ReactorExchangeResponseFunction;->responseFunction:Ljava/util/function/Function;", "FIELD:Lorg/springframework/web/service/invoker/HttpServiceMethod$ReactorExchangeResponseFunction;->returnTypeAdapter:Lorg/springframework/core/ReactiveAdapter;", "FIELD:Lorg/springframework/web/service/invoker/HttpServiceMethod$ReactorExchangeResponseFunction;->blockForOptional:Z", "FIELD:Lorg/springframework/web/service/invoker/HttpServiceMethod$ReactorExchangeResponseFunction;->blockTimeout:Ljava/time/Duration;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, ReactorExchangeResponseFunction.class, Object.class), ReactorExchangeResponseFunction.class, "responseFunction;returnTypeAdapter;blockForOptional;blockTimeout", "FIELD:Lorg/springframework/web/service/invoker/HttpServiceMethod$ReactorExchangeResponseFunction;->responseFunction:Ljava/util/function/Function;", "FIELD:Lorg/springframework/web/service/invoker/HttpServiceMethod$ReactorExchangeResponseFunction;->returnTypeAdapter:Lorg/springframework/core/ReactiveAdapter;", "FIELD:Lorg/springframework/web/service/invoker/HttpServiceMethod$ReactorExchangeResponseFunction;->blockForOptional:Z", "FIELD:Lorg/springframework/web/service/invoker/HttpServiceMethod$ReactorExchangeResponseFunction;->blockTimeout:Ljava/time/Duration;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public Function<HttpRequestValues, Publisher<?>> responseFunction() {
            return this.responseFunction;
        }

        @Nullable
        public ReactiveAdapter returnTypeAdapter() {
            return this.returnTypeAdapter;
        }

        public boolean blockForOptional() {
            return this.blockForOptional;
        }

        @Nullable
        public Duration blockTimeout() {
            return this.blockTimeout;
        }

        @Override // org.springframework.web.service.invoker.HttpServiceMethod.ResponseFunction
        @Nullable
        public Object execute(HttpRequestValues requestValues) {
            Publisher<?> responsePublisher = this.responseFunction.apply(requestValues);
            if (this.returnTypeAdapter != null) {
                return this.returnTypeAdapter.fromPublisher(responsePublisher);
            }
            if (this.blockForOptional) {
                if (this.blockTimeout != null) {
                    return ((Mono) responsePublisher).blockOptional(this.blockTimeout);
                }
                return ((Mono) responsePublisher).blockOptional();
            }
            if (this.blockTimeout != null) {
                return ((Mono) responsePublisher).block(this.blockTimeout);
            }
            return ((Mono) responsePublisher).block();
        }

        public static ResponseFunction create(ReactorHttpExchangeAdapter client, Method method) {
            Function<HttpRequestValues, Publisher<?>> responseFunction;
            MethodParameter returnParam = new MethodParameter(method, -1);
            Class<?> returnType = returnParam.getParameterType();
            boolean isSuspending = KotlinDetector.isSuspendingFunction(method);
            if (isSuspending) {
                returnType = Mono.class;
            }
            ReactiveAdapter reactiveAdapter = client.getReactiveAdapterRegistry().getAdapter(returnType);
            MethodParameter actualParam = reactiveAdapter != null ? returnParam.nested() : returnParam.nestedIfOptional();
            Class<?> actualType = isSuspending ? actualParam.getParameterType() : actualParam.getNestedParameterType();
            if (actualType.equals(Void.TYPE) || actualType.equals(Void.class)) {
                Objects.requireNonNull(client);
                responseFunction = client::exchangeForMono;
            } else if (reactiveAdapter != null && reactiveAdapter.isNoValue()) {
                Objects.requireNonNull(client);
                responseFunction = client::exchangeForMono;
            } else if (actualType.equals(HttpHeaders.class)) {
                Objects.requireNonNull(client);
                responseFunction = client::exchangeForHeadersMono;
            } else if (actualType.equals(ResponseEntity.class)) {
                MethodParameter bodyParam = isSuspending ? actualParam : actualParam.nested();
                Class<?> bodyType = bodyParam.getNestedParameterType();
                if (bodyType.equals(Void.class)) {
                    Objects.requireNonNull(client);
                    responseFunction = client::exchangeForBodilessEntityMono;
                } else {
                    ReactiveAdapter bodyAdapter = client.getReactiveAdapterRegistry().getAdapter(bodyType);
                    responseFunction = initResponseEntityFunction(client, bodyParam, bodyAdapter, isSuspending);
                }
            } else {
                responseFunction = initBodyFunction(client, actualParam, reactiveAdapter, isSuspending);
            }
            return new ReactorExchangeResponseFunction(responseFunction, reactiveAdapter, returnType.equals(Optional.class), client.getBlockTimeout());
        }

        private static Function<HttpRequestValues, Publisher<?>> initResponseEntityFunction(ReactorHttpExchangeAdapter client, MethodParameter methodParam, @Nullable ReactiveAdapter reactiveAdapter, boolean isSuspending) {
            if (reactiveAdapter == null) {
                return request -> {
                    return client.exchangeForEntityMono(request, ParameterizedTypeReference.forType(methodParam.getNestedGenericParameterType()));
                };
            }
            Assert.isTrue(reactiveAdapter.isMultiValue(), "ResponseEntity body must be a concrete value or a multi-value Publisher");
            ParameterizedTypeReference<?> bodyType = ParameterizedTypeReference.forType(isSuspending ? methodParam.nested().getGenericParameterType() : methodParam.nested().getNestedGenericParameterType());
            if (reactiveAdapter.getReactiveType().equals(Flux.class)) {
                return request2 -> {
                    return client.exchangeForEntityFlux(request2, bodyType);
                };
            }
            return request3 -> {
                return client.exchangeForEntityFlux(request3, bodyType).map(entity -> {
                    Object body = reactiveAdapter.fromPublisher((Publisher) entity.getBody());
                    return new ResponseEntity(body, entity.getHeaders(), entity.getStatusCode());
                });
            };
        }

        private static Function<HttpRequestValues, Publisher<?>> initBodyFunction(ReactorHttpExchangeAdapter client, MethodParameter methodParam, @Nullable ReactiveAdapter reactiveAdapter, boolean isSuspending) {
            ParameterizedTypeReference<?> bodyType = ParameterizedTypeReference.forType(isSuspending ? methodParam.getGenericParameterType() : methodParam.getNestedGenericParameterType());
            if (reactiveAdapter != null && reactiveAdapter.isMultiValue()) {
                return request -> {
                    return client.exchangeForBodyFlux(request, bodyType);
                };
            }
            return request2 -> {
                return client.exchangeForBodyMono(request2, bodyType);
            };
        }
    }
}
