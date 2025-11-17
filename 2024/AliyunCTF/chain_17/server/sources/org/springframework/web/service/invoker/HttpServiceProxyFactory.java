package org.springframework.web.service.invoker;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.core.KotlinDetector;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringValueResolver;
import org.springframework.web.service.annotation.HttpExchange;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/service/invoker/HttpServiceProxyFactory.class */
public final class HttpServiceProxyFactory {
    private final HttpExchangeAdapter exchangeAdapter;
    private final List<HttpServiceArgumentResolver> argumentResolvers;

    @Nullable
    private final StringValueResolver embeddedValueResolver;

    private HttpServiceProxyFactory(HttpExchangeAdapter exchangeAdapter, List<HttpServiceArgumentResolver> argumentResolvers, @Nullable StringValueResolver embeddedValueResolver) {
        this.exchangeAdapter = exchangeAdapter;
        this.argumentResolvers = argumentResolvers;
        this.embeddedValueResolver = embeddedValueResolver;
    }

    public <S> S createClient(Class<S> cls) {
        return (S) ProxyFactory.getProxy(cls, new HttpServiceMethodInterceptor(MethodIntrospector.selectMethods((Class<?>) cls, this::isExchangeMethod).stream().map(method -> {
            return createHttpServiceMethod(cls, method);
        }).toList()));
    }

    private boolean isExchangeMethod(Method method) {
        return AnnotatedElementUtils.hasAnnotation(method, HttpExchange.class);
    }

    private <S> HttpServiceMethod createHttpServiceMethod(Class<S> serviceType, Method method) {
        Assert.notNull(this.argumentResolvers, "No argument resolvers: afterPropertiesSet was not called");
        return new HttpServiceMethod(method, serviceType, this.argumentResolvers, this.exchangeAdapter, this.embeddedValueResolver);
    }

    public static Builder builderFor(HttpExchangeAdapter exchangeAdapter) {
        return new Builder().exchangeAdapter(exchangeAdapter);
    }

    @Deprecated(since = "6.1", forRemoval = true)
    public static Builder builder(HttpClientAdapter clientAdapter) {
        return new Builder().exchangeAdapter(clientAdapter.asReactorExchangeAdapter());
    }

    public static Builder builder() {
        return new Builder();
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/service/invoker/HttpServiceProxyFactory$Builder.class */
    public static final class Builder {

        @Nullable
        private HttpExchangeAdapter exchangeAdapter;
        private final List<HttpServiceArgumentResolver> customArgumentResolvers = new ArrayList();

        @Nullable
        private ConversionService conversionService;

        @Nullable
        private StringValueResolver embeddedValueResolver;

        private Builder() {
        }

        public Builder exchangeAdapter(HttpExchangeAdapter adapter) {
            this.exchangeAdapter = adapter;
            return this;
        }

        @Deprecated(since = "6.1", forRemoval = true)
        public Builder clientAdapter(HttpClientAdapter clientAdapter) {
            this.exchangeAdapter = clientAdapter.asReactorExchangeAdapter();
            return this;
        }

        public Builder customArgumentResolver(HttpServiceArgumentResolver resolver) {
            this.customArgumentResolvers.add(resolver);
            return this;
        }

        public Builder conversionService(ConversionService conversionService) {
            this.conversionService = conversionService;
            return this;
        }

        public Builder embeddedValueResolver(StringValueResolver embeddedValueResolver) {
            this.embeddedValueResolver = embeddedValueResolver;
            return this;
        }

        @Deprecated(since = "6.1", forRemoval = true)
        public Builder reactiveAdapterRegistry(ReactiveAdapterRegistry registry) {
            HttpExchangeAdapter httpExchangeAdapter = this.exchangeAdapter;
            if (httpExchangeAdapter instanceof AbstractReactorHttpExchangeAdapter) {
                AbstractReactorHttpExchangeAdapter settable = (AbstractReactorHttpExchangeAdapter) httpExchangeAdapter;
                settable.setReactiveAdapterRegistry(registry);
            }
            return this;
        }

        @Deprecated(since = "6.1", forRemoval = true)
        public Builder blockTimeout(@Nullable Duration blockTimeout) {
            HttpExchangeAdapter httpExchangeAdapter = this.exchangeAdapter;
            if (httpExchangeAdapter instanceof AbstractReactorHttpExchangeAdapter) {
                AbstractReactorHttpExchangeAdapter settable = (AbstractReactorHttpExchangeAdapter) httpExchangeAdapter;
                settable.setBlockTimeout(blockTimeout);
            }
            return this;
        }

        public HttpServiceProxyFactory build() {
            Assert.notNull(this.exchangeAdapter, "HttpClientAdapter is required");
            return new HttpServiceProxyFactory(this.exchangeAdapter, initArgumentResolvers(), this.embeddedValueResolver);
        }

        private List<HttpServiceArgumentResolver> initArgumentResolvers() {
            List<HttpServiceArgumentResolver> resolvers = new ArrayList<>(this.customArgumentResolvers);
            ConversionService service = this.conversionService != null ? this.conversionService : new DefaultFormattingConversionService();
            resolvers.add(new RequestHeaderArgumentResolver(service));
            resolvers.add(new RequestBodyArgumentResolver(this.exchangeAdapter));
            resolvers.add(new PathVariableArgumentResolver(service));
            resolvers.add(new RequestParamArgumentResolver(service));
            resolvers.add(new RequestPartArgumentResolver(this.exchangeAdapter));
            resolvers.add(new CookieValueArgumentResolver(service));
            if (this.exchangeAdapter.supportsRequestAttributes()) {
                resolvers.add(new RequestAttributeArgumentResolver());
            }
            resolvers.add(new UrlArgumentResolver());
            resolvers.add(new UriBuilderFactoryArgumentResolver());
            resolvers.add(new HttpMethodArgumentResolver());
            return resolvers;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/service/invoker/HttpServiceProxyFactory$HttpServiceMethodInterceptor.class */
    private static final class HttpServiceMethodInterceptor implements MethodInterceptor {
        private final Map<Method, HttpServiceMethod> httpServiceMethods;

        private HttpServiceMethodInterceptor(List<HttpServiceMethod> methods) {
            this.httpServiceMethods = (Map) methods.stream().collect(Collectors.toMap((v0) -> {
                return v0.getMethod();
            }, Function.identity()));
        }

        @Override // org.aopalliance.intercept.MethodInterceptor
        public Object invoke(MethodInvocation invocation) throws Throwable {
            Method method = invocation.getMethod();
            HttpServiceMethod httpServiceMethod = this.httpServiceMethods.get(method);
            if (httpServiceMethod != null) {
                Object[] arguments = KotlinDetector.isSuspendingFunction(method) ? resolveCoroutinesArguments(invocation.getArguments()) : invocation.getArguments();
                return httpServiceMethod.invoke(arguments);
            }
            if (method.isDefault() && (invocation instanceof ReflectiveMethodInvocation)) {
                ReflectiveMethodInvocation reflectiveMethodInvocation = (ReflectiveMethodInvocation) invocation;
                Object proxy = reflectiveMethodInvocation.getProxy();
                return InvocationHandler.invokeDefault(proxy, method, invocation.getArguments());
            }
            throw new IllegalStateException("Unexpected method invocation: " + method);
        }

        private static Object[] resolveCoroutinesArguments(Object[] args) {
            Object[] functionArgs = new Object[args.length - 1];
            System.arraycopy(args, 0, functionArgs, 0, args.length - 1);
            return functionArgs;
        }
    }
}
