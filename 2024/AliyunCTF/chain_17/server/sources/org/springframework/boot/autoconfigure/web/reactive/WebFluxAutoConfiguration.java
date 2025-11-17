package org.springframework.boot.autoconfigure.web.reactive;

import java.time.Duration;
import java.util.Objects;
import java.util.stream.Stream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.http.codec.CodecsAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.autoconfigure.template.TemplateAvailabilityProviders;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidatorAdapter;
import org.springframework.boot.autoconfigure.web.ConditionalOnEnabledResourceChain;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.WebResourcesRuntimeHints;
import org.springframework.boot.autoconfigure.web.format.DateTimeFormatters;
import org.springframework.boot.autoconfigure.web.format.WebConversionService;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.boot.web.reactive.filter.OrderedHiddenHttpMethodFilter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.annotation.Order;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.util.ClassUtils;
import org.springframework.validation.Validator;
import org.springframework.web.filter.reactive.HiddenHttpMethodFilter;
import org.springframework.web.reactive.config.BlockingExecutionConfigurer;
import org.springframework.web.reactive.config.DelegatingWebFluxConfiguration;
import org.springframework.web.reactive.config.ResourceHandlerRegistration;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.ViewResolverRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurationSupport;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.function.server.support.RouterFunctionMapping;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.WebSession;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;
import org.springframework.web.server.i18n.AcceptHeaderLocaleContextResolver;
import org.springframework.web.server.i18n.FixedLocaleContextResolver;
import org.springframework.web.server.i18n.LocaleContextResolver;
import org.springframework.web.server.session.DefaultWebSessionManager;
import org.springframework.web.server.session.InMemoryWebSessionStore;
import org.springframework.web.server.session.WebSessionIdResolver;
import org.springframework.web.server.session.WebSessionManager;
import reactor.core.publisher.Mono;

@AutoConfiguration(after = {ReactiveWebServerFactoryAutoConfiguration.class, CodecsAutoConfiguration.class, ReactiveMultipartAutoConfiguration.class, ValidationAutoConfiguration.class, WebSessionIdResolverAutoConfiguration.class})
@ConditionalOnClass({WebFluxConfigurer.class})
@ImportRuntimeHints({WebResourcesRuntimeHints.class})
@ConditionalOnMissingBean({WebFluxConfigurationSupport.class})
@AutoConfigureOrder(-2147483638)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/WebFluxAutoConfiguration.class */
public class WebFluxAutoConfiguration {
    @ConditionalOnMissingBean({HiddenHttpMethodFilter.class})
    @ConditionalOnProperty(prefix = "spring.webflux.hiddenmethod.filter", name = {"enabled"})
    @Bean
    public OrderedHiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new OrderedHiddenHttpMethodFilter();
    }

    @Configuration(proxyBeanMethods = false)
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/WebFluxAutoConfiguration$WelcomePageConfiguration.class */
    public static class WelcomePageConfiguration {
        @Bean
        public RouterFunctionMapping welcomePageRouterFunctionMapping(ApplicationContext applicationContext, WebFluxProperties webFluxProperties, WebProperties webProperties) {
            String[] staticLocations = webProperties.getResources().getStaticLocations();
            WelcomePageRouterFunctionFactory factory = new WelcomePageRouterFunctionFactory(new TemplateAvailabilityProviders(applicationContext), applicationContext, staticLocations, webFluxProperties.getStaticPathPattern());
            RouterFunction<ServerResponse> routerFunction = factory.createRouterFunction();
            if (routerFunction != null) {
                RouterFunctionMapping routerFunctionMapping = new RouterFunctionMapping(routerFunction);
                routerFunctionMapping.setOrder(1);
                return routerFunctionMapping;
            }
            return null;
        }
    }

    @EnableConfigurationProperties({WebProperties.class, WebFluxProperties.class})
    @Configuration(proxyBeanMethods = false)
    @Import({EnableWebFluxConfiguration.class})
    @Order(0)
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/WebFluxAutoConfiguration$WebFluxConfig.class */
    public static class WebFluxConfig implements WebFluxConfigurer {
        private static final Log logger = LogFactory.getLog((Class<?>) WebFluxConfig.class);
        private final WebProperties.Resources resourceProperties;
        private final WebFluxProperties webFluxProperties;
        private final ListableBeanFactory beanFactory;
        private final ObjectProvider<HandlerMethodArgumentResolver> argumentResolvers;
        private final ObjectProvider<CodecCustomizer> codecCustomizers;
        private final ResourceHandlerRegistrationCustomizer resourceHandlerRegistrationCustomizer;
        private final ObjectProvider<ViewResolver> viewResolvers;

        public WebFluxConfig(WebProperties webProperties, WebFluxProperties webFluxProperties, ListableBeanFactory beanFactory, ObjectProvider<HandlerMethodArgumentResolver> resolvers, ObjectProvider<CodecCustomizer> codecCustomizers, ObjectProvider<ResourceHandlerRegistrationCustomizer> resourceHandlerRegistrationCustomizer, ObjectProvider<ViewResolver> viewResolvers) {
            this.resourceProperties = webProperties.getResources();
            this.webFluxProperties = webFluxProperties;
            this.beanFactory = beanFactory;
            this.argumentResolvers = resolvers;
            this.codecCustomizers = codecCustomizers;
            this.resourceHandlerRegistrationCustomizer = resourceHandlerRegistrationCustomizer.getIfAvailable();
            this.viewResolvers = viewResolvers;
        }

        public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
            Stream<HandlerMethodArgumentResolver> orderedStream = this.argumentResolvers.orderedStream();
            Objects.requireNonNull(configurer);
            orderedStream.forEach(xva$0 -> {
                configurer.addCustomResolver(new HandlerMethodArgumentResolver[]{xva$0});
            });
        }

        public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
            this.codecCustomizers.orderedStream().forEach(customizer -> {
                customizer.customize(configurer);
            });
        }

        public void configureBlockingExecution(BlockingExecutionConfigurer configurer) {
            if (this.beanFactory.containsBean(TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME)) {
                Object taskExecutor = this.beanFactory.getBean(TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME);
                if (taskExecutor instanceof AsyncTaskExecutor) {
                    AsyncTaskExecutor asyncTaskExecutor = (AsyncTaskExecutor) taskExecutor;
                    configurer.setExecutor(asyncTaskExecutor);
                }
            }
        }

        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            if (!this.resourceProperties.isAddMappings()) {
                logger.debug("Default resource handling disabled");
                return;
            }
            String webjarsPathPattern = this.webFluxProperties.getWebjarsPathPattern();
            if (!registry.hasMappingForPattern(webjarsPathPattern)) {
                ResourceHandlerRegistration registration = registry.addResourceHandler(new String[]{webjarsPathPattern}).addResourceLocations(new String[]{"classpath:/META-INF/resources/webjars/"});
                configureResourceCaching(registration);
                customizeResourceHandlerRegistration(registration);
            }
            String staticPathPattern = this.webFluxProperties.getStaticPathPattern();
            if (!registry.hasMappingForPattern(staticPathPattern)) {
                ResourceHandlerRegistration registration2 = registry.addResourceHandler(new String[]{staticPathPattern}).addResourceLocations(this.resourceProperties.getStaticLocations());
                configureResourceCaching(registration2);
                customizeResourceHandlerRegistration(registration2);
            }
        }

        private void configureResourceCaching(ResourceHandlerRegistration registration) {
            Duration cachePeriod = this.resourceProperties.getCache().getPeriod();
            WebProperties.Resources.Cache.Cachecontrol cacheControl = this.resourceProperties.getCache().getCachecontrol();
            if (cachePeriod != null && cacheControl.getMaxAge() == null) {
                cacheControl.setMaxAge(cachePeriod);
            }
            registration.setCacheControl(cacheControl.toHttpCacheControl());
            registration.setUseLastModified(this.resourceProperties.getCache().isUseLastModified());
        }

        public void configureViewResolvers(ViewResolverRegistry registry) {
            Stream<ViewResolver> orderedStream = this.viewResolvers.orderedStream();
            Objects.requireNonNull(registry);
            orderedStream.forEach(registry::viewResolver);
        }

        public void addFormatters(FormatterRegistry registry) {
            ApplicationConversionService.addBeans(registry, this.beanFactory);
        }

        private void customizeResourceHandlerRegistration(ResourceHandlerRegistration registration) {
            if (this.resourceHandlerRegistrationCustomizer != null) {
                this.resourceHandlerRegistrationCustomizer.customize(registration);
            }
        }
    }

    @EnableConfigurationProperties({WebProperties.class, ServerProperties.class})
    @Configuration(proxyBeanMethods = false)
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/WebFluxAutoConfiguration$EnableWebFluxConfiguration.class */
    public static class EnableWebFluxConfiguration extends DelegatingWebFluxConfiguration {
        private final WebFluxProperties webFluxProperties;
        private final WebProperties webProperties;
        private final ServerProperties serverProperties;
        private final WebFluxRegistrations webFluxRegistrations;

        public EnableWebFluxConfiguration(WebFluxProperties webFluxProperties, WebProperties webProperties, ServerProperties serverProperties, ObjectProvider<WebFluxRegistrations> webFluxRegistrations) {
            this.webFluxProperties = webFluxProperties;
            this.webProperties = webProperties;
            this.serverProperties = serverProperties;
            this.webFluxRegistrations = webFluxRegistrations.getIfUnique();
        }

        @Bean
        public FormattingConversionService webFluxConversionService() {
            WebFluxProperties.Format format = this.webFluxProperties.getFormat();
            WebConversionService conversionService = new WebConversionService(new DateTimeFormatters().dateFormat(format.getDate()).timeFormat(format.getTime()).dateTimeFormat(format.getDateTime()));
            addFormatters(conversionService);
            return conversionService;
        }

        @Bean
        public Validator webFluxValidator() {
            if (!ClassUtils.isPresent("jakarta.validation.Validator", getClass().getClassLoader())) {
                return super.webFluxValidator();
            }
            return ValidatorAdapter.get(getApplicationContext(), getValidator());
        }

        protected RequestMappingHandlerAdapter createRequestMappingHandlerAdapter() {
            RequestMappingHandlerAdapter adapter;
            if (this.webFluxRegistrations != null && (adapter = this.webFluxRegistrations.getRequestMappingHandlerAdapter()) != null) {
                return adapter;
            }
            return super.createRequestMappingHandlerAdapter();
        }

        protected RequestMappingHandlerMapping createRequestMappingHandlerMapping() {
            RequestMappingHandlerMapping mapping;
            if (this.webFluxRegistrations != null && (mapping = this.webFluxRegistrations.getRequestMappingHandlerMapping()) != null) {
                return mapping;
            }
            return super.createRequestMappingHandlerMapping();
        }

        @ConditionalOnMissingBean(name = {WebHttpHandlerBuilder.LOCALE_CONTEXT_RESOLVER_BEAN_NAME})
        @Bean
        public LocaleContextResolver localeContextResolver() {
            if (this.webProperties.getLocaleResolver() == WebProperties.LocaleResolver.FIXED) {
                return new FixedLocaleContextResolver(this.webProperties.getLocale());
            }
            AcceptHeaderLocaleContextResolver localeContextResolver = new AcceptHeaderLocaleContextResolver();
            localeContextResolver.setDefaultLocale(this.webProperties.getLocale());
            return localeContextResolver;
        }

        @ConditionalOnMissingBean(name = {WebHttpHandlerBuilder.WEB_SESSION_MANAGER_BEAN_NAME})
        @Bean
        public WebSessionManager webSessionManager(ObjectProvider<WebSessionIdResolver> webSessionIdResolver) {
            DefaultWebSessionManager webSessionManager = new DefaultWebSessionManager();
            Duration timeout = this.serverProperties.getReactive().getSession().getTimeout();
            webSessionManager.setSessionStore(new MaxIdleTimeInMemoryWebSessionStore(timeout));
            Objects.requireNonNull(webSessionManager);
            webSessionIdResolver.ifAvailable(webSessionManager::setSessionIdResolver);
            return webSessionManager;
        }
    }

    @ConditionalOnEnabledResourceChain
    @Configuration(proxyBeanMethods = false)
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/WebFluxAutoConfiguration$ResourceChainCustomizerConfiguration.class */
    static class ResourceChainCustomizerConfiguration {
        ResourceChainCustomizerConfiguration() {
        }

        @Bean
        ResourceChainResourceHandlerRegistrationCustomizer resourceHandlerRegistrationCustomizer(WebProperties webProperties) {
            return new ResourceChainResourceHandlerRegistrationCustomizer(webProperties.getResources());
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = "spring.webflux.problemdetails", name = {"enabled"}, havingValue = "true")
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/WebFluxAutoConfiguration$ProblemDetailsErrorHandlingConfiguration.class */
    static class ProblemDetailsErrorHandlingConfiguration {
        ProblemDetailsErrorHandlingConfiguration() {
        }

        @ConditionalOnMissingBean({ResponseEntityExceptionHandler.class})
        @Bean
        @Order(0)
        ProblemDetailsExceptionHandler problemDetailsExceptionHandler() {
            return new ProblemDetailsExceptionHandler();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/WebFluxAutoConfiguration$MaxIdleTimeInMemoryWebSessionStore.class */
    static final class MaxIdleTimeInMemoryWebSessionStore extends InMemoryWebSessionStore {
        private final Duration timeout;

        private MaxIdleTimeInMemoryWebSessionStore(Duration timeout) {
            this.timeout = timeout;
        }

        @Override // org.springframework.web.server.session.InMemoryWebSessionStore, org.springframework.web.server.session.WebSessionStore
        public Mono<WebSession> createWebSession() {
            return super.createWebSession().doOnSuccess(this::setMaxIdleTime);
        }

        private void setMaxIdleTime(WebSession session) {
            session.setMaxIdleTime(this.timeout);
        }
    }
}
