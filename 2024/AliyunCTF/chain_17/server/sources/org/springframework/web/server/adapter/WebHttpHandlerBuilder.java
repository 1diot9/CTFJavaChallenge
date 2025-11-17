package org.springframework.web.server.adapter;

import io.micrometer.observation.ObservationRegistry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cglib.core.Constants;
import org.springframework.context.ApplicationContext;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.HttpHandlerDecoratorFactory;
import org.springframework.http.server.reactive.observation.ServerRequestObservationConvention;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.WebExceptionHandler;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebHandler;
import org.springframework.web.server.handler.ExceptionHandlingWebHandler;
import org.springframework.web.server.handler.FilteringWebHandler;
import org.springframework.web.server.i18n.LocaleContextResolver;
import org.springframework.web.server.session.WebSessionManager;
import reactor.blockhound.BlockHound;
import reactor.blockhound.integration.BlockHoundIntegration;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/adapter/WebHttpHandlerBuilder.class */
public final class WebHttpHandlerBuilder {
    public static final String WEB_HANDLER_BEAN_NAME = "webHandler";
    public static final String WEB_SESSION_MANAGER_BEAN_NAME = "webSessionManager";
    public static final String SERVER_CODEC_CONFIGURER_BEAN_NAME = "serverCodecConfigurer";
    public static final String LOCALE_CONTEXT_RESOLVER_BEAN_NAME = "localeContextResolver";
    public static final String FORWARDED_HEADER_TRANSFORMER_BEAN_NAME = "forwardedHeaderTransformer";
    private final WebHandler webHandler;

    @Nullable
    private final ApplicationContext applicationContext;
    private final List<WebFilter> filters = new ArrayList();
    private final List<WebExceptionHandler> exceptionHandlers = new ArrayList();

    @Nullable
    private Function<HttpHandler, HttpHandler> httpHandlerDecorator;

    @Nullable
    private WebSessionManager sessionManager;

    @Nullable
    private ServerCodecConfigurer codecConfigurer;

    @Nullable
    private LocaleContextResolver localeContextResolver;

    @Nullable
    private ForwardedHeaderTransformer forwardedHeaderTransformer;

    @Nullable
    private ObservationRegistry observationRegistry;

    @Nullable
    private ServerRequestObservationConvention observationConvention;

    private WebHttpHandlerBuilder(WebHandler webHandler, @Nullable ApplicationContext applicationContext) {
        Assert.notNull(webHandler, "WebHandler must not be null");
        this.webHandler = webHandler;
        this.applicationContext = applicationContext;
    }

    private WebHttpHandlerBuilder(WebHttpHandlerBuilder other) {
        this.webHandler = other.webHandler;
        this.applicationContext = other.applicationContext;
        this.filters.addAll(other.filters);
        this.exceptionHandlers.addAll(other.exceptionHandlers);
        this.sessionManager = other.sessionManager;
        this.codecConfigurer = other.codecConfigurer;
        this.localeContextResolver = other.localeContextResolver;
        this.forwardedHeaderTransformer = other.forwardedHeaderTransformer;
        this.observationRegistry = other.observationRegistry;
        this.observationConvention = other.observationConvention;
        this.httpHandlerDecorator = other.httpHandlerDecorator;
    }

    public static WebHttpHandlerBuilder webHandler(WebHandler webHandler) {
        return new WebHttpHandlerBuilder(webHandler, null);
    }

    public static WebHttpHandlerBuilder applicationContext(ApplicationContext context) {
        WebHttpHandlerBuilder builder = new WebHttpHandlerBuilder((WebHandler) context.getBean(WEB_HANDLER_BEAN_NAME, WebHandler.class), context);
        List<WebFilter> webFilters = context.getBeanProvider(WebFilter.class).orderedStream().toList();
        builder.filters(filters -> {
            filters.addAll(webFilters);
        });
        List<WebExceptionHandler> exceptionHandlers = context.getBeanProvider(WebExceptionHandler.class).orderedStream().toList();
        builder.exceptionHandlers(handlers -> {
            handlers.addAll(exceptionHandlers);
        });
        Stream orderedStream = context.getBeanProvider(HttpHandlerDecoratorFactory.class).orderedStream();
        Objects.requireNonNull(builder);
        orderedStream.forEach((v1) -> {
            r1.httpHandlerDecorator(v1);
        });
        ObjectProvider beanProvider = context.getBeanProvider(ObservationRegistry.class);
        Objects.requireNonNull(builder);
        beanProvider.ifUnique(builder::observationRegistry);
        ObjectProvider beanProvider2 = context.getBeanProvider(ServerRequestObservationConvention.class);
        Objects.requireNonNull(builder);
        beanProvider2.ifAvailable(builder::observationConvention);
        try {
            builder.sessionManager((WebSessionManager) context.getBean(WEB_SESSION_MANAGER_BEAN_NAME, WebSessionManager.class));
        } catch (NoSuchBeanDefinitionException e) {
        }
        try {
            builder.codecConfigurer((ServerCodecConfigurer) context.getBean(SERVER_CODEC_CONFIGURER_BEAN_NAME, ServerCodecConfigurer.class));
        } catch (NoSuchBeanDefinitionException e2) {
        }
        try {
            builder.localeContextResolver((LocaleContextResolver) context.getBean(LOCALE_CONTEXT_RESOLVER_BEAN_NAME, LocaleContextResolver.class));
        } catch (NoSuchBeanDefinitionException e3) {
        }
        try {
            builder.forwardedHeaderTransformer((ForwardedHeaderTransformer) context.getBean(FORWARDED_HEADER_TRANSFORMER_BEAN_NAME, ForwardedHeaderTransformer.class));
        } catch (NoSuchBeanDefinitionException e4) {
        }
        return builder;
    }

    public WebHttpHandlerBuilder filter(WebFilter... filters) {
        if (!ObjectUtils.isEmpty((Object[]) filters)) {
            this.filters.addAll(Arrays.asList(filters));
            updateFilters();
        }
        return this;
    }

    public WebHttpHandlerBuilder filters(Consumer<List<WebFilter>> consumer) {
        consumer.accept(this.filters);
        updateFilters();
        return this;
    }

    private void updateFilters() {
        if (this.filters.isEmpty()) {
            return;
        }
        List<WebFilter> filtersToUse = this.filters.stream().peek(webFilter -> {
            if (webFilter instanceof ForwardedHeaderTransformer) {
                ForwardedHeaderTransformer forwardedHeaderTransformerFilter = (ForwardedHeaderTransformer) webFilter;
                if (this.forwardedHeaderTransformer == null) {
                    this.forwardedHeaderTransformer = forwardedHeaderTransformerFilter;
                }
            }
        }).filter(filter -> {
            return !(filter instanceof ForwardedHeaderTransformer);
        }).toList();
        this.filters.clear();
        this.filters.addAll(filtersToUse);
    }

    public WebHttpHandlerBuilder exceptionHandler(WebExceptionHandler... handlers) {
        if (!ObjectUtils.isEmpty((Object[]) handlers)) {
            this.exceptionHandlers.addAll(Arrays.asList(handlers));
        }
        return this;
    }

    public WebHttpHandlerBuilder exceptionHandlers(Consumer<List<WebExceptionHandler>> consumer) {
        consumer.accept(this.exceptionHandlers);
        return this;
    }

    public WebHttpHandlerBuilder sessionManager(WebSessionManager manager) {
        this.sessionManager = manager;
        return this;
    }

    public boolean hasSessionManager() {
        return this.sessionManager != null;
    }

    public WebHttpHandlerBuilder codecConfigurer(ServerCodecConfigurer codecConfigurer) {
        this.codecConfigurer = codecConfigurer;
        return this;
    }

    public boolean hasCodecConfigurer() {
        return this.codecConfigurer != null;
    }

    public WebHttpHandlerBuilder localeContextResolver(LocaleContextResolver localeContextResolver) {
        this.localeContextResolver = localeContextResolver;
        return this;
    }

    public boolean hasLocaleContextResolver() {
        return this.localeContextResolver != null;
    }

    public WebHttpHandlerBuilder forwardedHeaderTransformer(ForwardedHeaderTransformer transformer) {
        this.forwardedHeaderTransformer = transformer;
        return this;
    }

    public boolean hasForwardedHeaderTransformer() {
        return this.forwardedHeaderTransformer != null;
    }

    public WebHttpHandlerBuilder observationRegistry(ObservationRegistry observationRegistry) {
        this.observationRegistry = observationRegistry;
        return this;
    }

    public WebHttpHandlerBuilder observationConvention(ServerRequestObservationConvention observationConvention) {
        this.observationConvention = observationConvention;
        return this;
    }

    public WebHttpHandlerBuilder httpHandlerDecorator(Function<HttpHandler, HttpHandler> handlerDecorator) {
        this.httpHandlerDecorator = this.httpHandlerDecorator != null ? handlerDecorator.andThen(this.httpHandlerDecorator) : handlerDecorator;
        return this;
    }

    public boolean hasHttpHandlerDecorator() {
        return this.httpHandlerDecorator != null;
    }

    public HttpHandler build() {
        WebHandler decorated = new FilteringWebHandler(this.webHandler, this.filters);
        HttpWebHandlerAdapter adapted = new HttpWebHandlerAdapter(new ExceptionHandlingWebHandler(decorated, this.exceptionHandlers));
        if (this.sessionManager != null) {
            adapted.setSessionManager(this.sessionManager);
        }
        if (this.codecConfigurer != null) {
            adapted.setCodecConfigurer(this.codecConfigurer);
        }
        if (this.localeContextResolver != null) {
            adapted.setLocaleContextResolver(this.localeContextResolver);
        }
        if (this.forwardedHeaderTransformer != null) {
            adapted.setForwardedHeaderTransformer(this.forwardedHeaderTransformer);
        }
        if (this.observationRegistry != null) {
            adapted.setObservationRegistry(this.observationRegistry);
        }
        if (this.observationConvention != null) {
            adapted.setObservationConvention(this.observationConvention);
        }
        if (this.applicationContext != null) {
            adapted.setApplicationContext(this.applicationContext);
        }
        adapted.afterPropertiesSet();
        return this.httpHandlerDecorator != null ? this.httpHandlerDecorator.apply(adapted) : adapted;
    }

    /* renamed from: clone, reason: merged with bridge method [inline-methods] */
    public WebHttpHandlerBuilder m2827clone() {
        return new WebHttpHandlerBuilder(this);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/adapter/WebHttpHandlerBuilder$SpringWebBlockHoundIntegration.class */
    public static class SpringWebBlockHoundIntegration implements BlockHoundIntegration {
        public void applyTo(BlockHound.Builder builder) {
            builder.allowBlockingCallsInside("org.springframework.http.MediaTypeFactory", Constants.STATIC_NAME);
            builder.allowBlockingCallsInside("org.springframework.web.util.HtmlUtils", Constants.STATIC_NAME);
        }
    }
}
