package org.springframework.boot.autoconfigure.web.servlet;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import java.time.Duration;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.autoconfigure.template.TemplateAvailabilityProviders;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidatorAdapter;
import org.springframework.boot.autoconfigure.web.ConditionalOnEnabledResourceChain;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.WebResourcesRuntimeHints;
import org.springframework.boot.autoconfigure.web.format.DateTimeFormatters;
import org.springframework.boot.autoconfigure.web.format.WebConversionService;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.filter.OrderedFormContentFilter;
import org.springframework.boot.web.servlet.filter.OrderedHiddenHttpMethodFilter;
import org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ClassUtils;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.PathExtensionContentNegotiationStrategy;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.filter.FormContentFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.FlashMapManager;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ThemeResolver;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceChainRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.EncodedResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.springframework.web.servlet.resource.ResourceUrlProvider;
import org.springframework.web.servlet.resource.VersionResourceResolver;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.util.UrlPathHelper;

@AutoConfiguration(after = {DispatcherServletAutoConfiguration.class, TaskExecutionAutoConfiguration.class, ValidationAutoConfiguration.class})
@ConditionalOnClass({Servlet.class, DispatcherServlet.class, WebMvcConfigurer.class})
@ImportRuntimeHints({WebResourcesRuntimeHints.class})
@ConditionalOnMissingBean({WebMvcConfigurationSupport.class})
@AutoConfigureOrder(-2147483638)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/WebMvcAutoConfiguration.class */
public class WebMvcAutoConfiguration {
    public static final String DEFAULT_PREFIX = "";
    public static final String DEFAULT_SUFFIX = "";
    private static final String SERVLET_LOCATION = "/";

    /* JADX INFO: Access modifiers changed from: package-private */
    @FunctionalInterface
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/WebMvcAutoConfiguration$ResourceHandlerRegistrationCustomizer.class */
    public interface ResourceHandlerRegistrationCustomizer {
        void customize(ResourceHandlerRegistration registration);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @FunctionalInterface
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/WebMvcAutoConfiguration$WelcomePageHandlerMappingFactory.class */
    public interface WelcomePageHandlerMappingFactory<T extends AbstractUrlHandlerMapping> {
        T create(TemplateAvailabilityProviders templateAvailabilityProviders, ApplicationContext applicationContext, Resource indexHtmlResource, String staticPathPattern);
    }

    @ConditionalOnMissingBean({HiddenHttpMethodFilter.class})
    @ConditionalOnProperty(prefix = "spring.mvc.hiddenmethod.filter", name = {"enabled"})
    @Bean
    public OrderedHiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new OrderedHiddenHttpMethodFilter();
    }

    @ConditionalOnMissingBean({FormContentFilter.class})
    @ConditionalOnProperty(prefix = "spring.mvc.formcontent.filter", name = {"enabled"}, matchIfMissing = true)
    @Bean
    public OrderedFormContentFilter formContentFilter() {
        return new OrderedFormContentFilter();
    }

    @EnableConfigurationProperties({WebMvcProperties.class, WebProperties.class})
    @Configuration(proxyBeanMethods = false)
    @Import({EnableWebMvcConfiguration.class})
    @Order(0)
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/WebMvcAutoConfiguration$WebMvcAutoConfigurationAdapter.class */
    public static class WebMvcAutoConfigurationAdapter implements WebMvcConfigurer, ServletContextAware {
        private static final Log logger = LogFactory.getLog((Class<?>) WebMvcConfigurer.class);
        private final WebProperties.Resources resourceProperties;
        private final WebMvcProperties mvcProperties;
        private final ListableBeanFactory beanFactory;
        private final ObjectProvider<HttpMessageConverters> messageConvertersProvider;
        private final ObjectProvider<DispatcherServletPath> dispatcherServletPath;
        private final ObjectProvider<ServletRegistrationBean<?>> servletRegistrations;
        private final ResourceHandlerRegistrationCustomizer resourceHandlerRegistrationCustomizer;
        private ServletContext servletContext;

        public WebMvcAutoConfigurationAdapter(WebProperties webProperties, WebMvcProperties mvcProperties, ListableBeanFactory beanFactory, ObjectProvider<HttpMessageConverters> messageConvertersProvider, ObjectProvider<ResourceHandlerRegistrationCustomizer> resourceHandlerRegistrationCustomizerProvider, ObjectProvider<DispatcherServletPath> dispatcherServletPath, ObjectProvider<ServletRegistrationBean<?>> servletRegistrations) {
            this.resourceProperties = webProperties.getResources();
            this.mvcProperties = mvcProperties;
            this.beanFactory = beanFactory;
            this.messageConvertersProvider = messageConvertersProvider;
            this.resourceHandlerRegistrationCustomizer = resourceHandlerRegistrationCustomizerProvider.getIfAvailable();
            this.dispatcherServletPath = dispatcherServletPath;
            this.servletRegistrations = servletRegistrations;
        }

        @Override // org.springframework.web.context.ServletContextAware
        public void setServletContext(ServletContext servletContext) {
            this.servletContext = servletContext;
        }

        @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
        public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
            this.messageConvertersProvider.ifAvailable(customConverters -> {
                converters.addAll(customConverters.getConverters());
            });
        }

        @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
        public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
            if (this.beanFactory.containsBean(TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME)) {
                Object taskExecutor = this.beanFactory.getBean(TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME);
                if (taskExecutor instanceof AsyncTaskExecutor) {
                    AsyncTaskExecutor asyncTaskExecutor = (AsyncTaskExecutor) taskExecutor;
                    configurer.setTaskExecutor(asyncTaskExecutor);
                }
            }
            Duration timeout = this.mvcProperties.getAsync().getRequestTimeout();
            if (timeout != null) {
                configurer.setDefaultTimeout(timeout.toMillis());
            }
        }

        @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
        public void configurePathMatch(PathMatchConfigurer configurer) {
            if (this.mvcProperties.getPathmatch().getMatchingStrategy() == WebMvcProperties.MatchingStrategy.ANT_PATH_MATCHER) {
                configurer.setPathMatcher(new AntPathMatcher());
                this.dispatcherServletPath.ifAvailable(dispatcherPath -> {
                    String servletUrlMapping = dispatcherPath.getServletUrlMapping();
                    if (servletUrlMapping.equals("/") && singleDispatcherServlet()) {
                        UrlPathHelper urlPathHelper = new UrlPathHelper();
                        urlPathHelper.setAlwaysUseFullPath(true);
                        configurer.setUrlPathHelper(urlPathHelper);
                    }
                });
            }
        }

        private boolean singleDispatcherServlet() {
            Stream<R> map = this.servletRegistrations.stream().map((v0) -> {
                return v0.getServlet();
            });
            Class<DispatcherServlet> cls = DispatcherServlet.class;
            Objects.requireNonNull(DispatcherServlet.class);
            return map.filter((v1) -> {
                return r1.isInstance(v1);
            }).count() == 1;
        }

        @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
        public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
            WebMvcProperties.Contentnegotiation contentnegotiation = this.mvcProperties.getContentnegotiation();
            configurer.favorParameter(contentnegotiation.isFavorParameter());
            if (contentnegotiation.getParameterName() != null) {
                configurer.parameterName(contentnegotiation.getParameterName());
            }
            Map<String, MediaType> mediaTypes = this.mvcProperties.getContentnegotiation().getMediaTypes();
            Objects.requireNonNull(configurer);
            mediaTypes.forEach(configurer::mediaType);
        }

        @ConditionalOnMissingBean
        @Bean
        public InternalResourceViewResolver defaultViewResolver() {
            InternalResourceViewResolver resolver = new InternalResourceViewResolver();
            resolver.setPrefix(this.mvcProperties.getView().getPrefix());
            resolver.setSuffix(this.mvcProperties.getView().getSuffix());
            return resolver;
        }

        @ConditionalOnMissingBean
        @ConditionalOnBean({View.class})
        @Bean
        public BeanNameViewResolver beanNameViewResolver() {
            BeanNameViewResolver resolver = new BeanNameViewResolver();
            resolver.setOrder(2147483637);
            return resolver;
        }

        @ConditionalOnMissingBean(name = {DispatcherServlet.VIEW_RESOLVER_BEAN_NAME}, value = {ContentNegotiatingViewResolver.class})
        @ConditionalOnBean({ViewResolver.class})
        @Bean
        public ContentNegotiatingViewResolver viewResolver(BeanFactory beanFactory) {
            ContentNegotiatingViewResolver resolver = new ContentNegotiatingViewResolver();
            resolver.setContentNegotiationManager((ContentNegotiationManager) beanFactory.getBean(ContentNegotiationManager.class));
            resolver.setOrder(Integer.MIN_VALUE);
            return resolver;
        }

        @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
        public MessageCodesResolver getMessageCodesResolver() {
            if (this.mvcProperties.getMessageCodesResolverFormat() != null) {
                DefaultMessageCodesResolver resolver = new DefaultMessageCodesResolver();
                resolver.setMessageCodeFormatter(this.mvcProperties.getMessageCodesResolverFormat());
                return resolver;
            }
            return null;
        }

        @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
        public void addFormatters(FormatterRegistry registry) {
            ApplicationConversionService.addBeans(registry, this.beanFactory);
        }

        @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            if (!this.resourceProperties.isAddMappings()) {
                logger.debug("Default resource handling disabled");
            } else {
                addResourceHandler(registry, this.mvcProperties.getWebjarsPathPattern(), "classpath:/META-INF/resources/webjars/");
                addResourceHandler(registry, this.mvcProperties.getStaticPathPattern(), registration -> {
                    registration.addResourceLocations(this.resourceProperties.getStaticLocations());
                    if (this.servletContext != null) {
                        ServletContextResource resource = new ServletContextResource(this.servletContext, "/");
                        registration.addResourceLocations(resource);
                    }
                });
            }
        }

        private void addResourceHandler(ResourceHandlerRegistry registry, String pattern, String... locations) {
            addResourceHandler(registry, pattern, registration -> {
                registration.addResourceLocations(locations);
            });
        }

        private void addResourceHandler(ResourceHandlerRegistry registry, String pattern, Consumer<ResourceHandlerRegistration> customizer) {
            if (registry.hasMappingForPattern(pattern)) {
                return;
            }
            ResourceHandlerRegistration registration = registry.addResourceHandler(pattern);
            customizer.accept(registration);
            registration.setCachePeriod(getSeconds(this.resourceProperties.getCache().getPeriod()));
            registration.setCacheControl(this.resourceProperties.getCache().getCachecontrol().toHttpCacheControl());
            registration.setUseLastModified(this.resourceProperties.getCache().isUseLastModified());
            customizeResourceHandlerRegistration(registration);
        }

        private Integer getSeconds(Duration cachePeriod) {
            if (cachePeriod != null) {
                return Integer.valueOf((int) cachePeriod.getSeconds());
            }
            return null;
        }

        private void customizeResourceHandlerRegistration(ResourceHandlerRegistration registration) {
            if (this.resourceHandlerRegistrationCustomizer != null) {
                this.resourceHandlerRegistrationCustomizer.customize(registration);
            }
        }

        @ConditionalOnMissingBean({RequestContextListener.class, RequestContextFilter.class})
        @ConditionalOnMissingFilterBean({RequestContextFilter.class})
        @Bean
        public static RequestContextFilter requestContextFilter() {
            return new OrderedRequestContextFilter();
        }
    }

    @EnableConfigurationProperties({WebProperties.class})
    @Configuration(proxyBeanMethods = false)
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/WebMvcAutoConfiguration$EnableWebMvcConfiguration.class */
    public static class EnableWebMvcConfiguration extends DelegatingWebMvcConfiguration implements ResourceLoaderAware {
        private final WebProperties.Resources resourceProperties;
        private final WebMvcProperties mvcProperties;
        private final WebProperties webProperties;
        private final ListableBeanFactory beanFactory;
        private final WebMvcRegistrations mvcRegistrations;
        private ResourceLoader resourceLoader;

        public EnableWebMvcConfiguration(WebMvcProperties mvcProperties, WebProperties webProperties, ObjectProvider<WebMvcRegistrations> mvcRegistrationsProvider, ObjectProvider<ResourceHandlerRegistrationCustomizer> resourceHandlerRegistrationCustomizerProvider, ListableBeanFactory beanFactory) {
            this.resourceProperties = webProperties.getResources();
            this.mvcProperties = mvcProperties;
            this.webProperties = webProperties;
            this.mvcRegistrations = mvcRegistrationsProvider.getIfUnique();
            this.beanFactory = beanFactory;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
        public RequestMappingHandlerAdapter createRequestMappingHandlerAdapter() {
            RequestMappingHandlerAdapter adapter;
            if (this.mvcRegistrations != null && (adapter = this.mvcRegistrations.getRequestMappingHandlerAdapter()) != null) {
                return adapter;
            }
            return super.createRequestMappingHandlerAdapter();
        }

        @Bean
        public WelcomePageHandlerMapping welcomePageHandlerMapping(ApplicationContext applicationContext, FormattingConversionService mvcConversionService, ResourceUrlProvider mvcResourceUrlProvider) {
            return (WelcomePageHandlerMapping) createWelcomePageHandlerMapping(applicationContext, mvcConversionService, mvcResourceUrlProvider, WelcomePageHandlerMapping::new);
        }

        @Bean
        public WelcomePageNotAcceptableHandlerMapping welcomePageNotAcceptableHandlerMapping(ApplicationContext applicationContext, FormattingConversionService mvcConversionService, ResourceUrlProvider mvcResourceUrlProvider) {
            return (WelcomePageNotAcceptableHandlerMapping) createWelcomePageHandlerMapping(applicationContext, mvcConversionService, mvcResourceUrlProvider, WelcomePageNotAcceptableHandlerMapping::new);
        }

        private <T extends AbstractUrlHandlerMapping> T createWelcomePageHandlerMapping(ApplicationContext applicationContext, FormattingConversionService mvcConversionService, ResourceUrlProvider mvcResourceUrlProvider, WelcomePageHandlerMappingFactory<T> factory) {
            TemplateAvailabilityProviders templateAvailabilityProviders = new TemplateAvailabilityProviders(applicationContext);
            String staticPathPattern = this.mvcProperties.getStaticPathPattern();
            T handlerMapping = factory.create(templateAvailabilityProviders, applicationContext, getIndexHtmlResource(), staticPathPattern);
            handlerMapping.setInterceptors(getInterceptors(mvcConversionService, mvcResourceUrlProvider));
            handlerMapping.setCorsConfigurations(getCorsConfigurations());
            return handlerMapping;
        }

        @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
        @ConditionalOnMissingBean(name = {DispatcherServlet.LOCALE_RESOLVER_BEAN_NAME})
        @Bean
        public LocaleResolver localeResolver() {
            if (this.webProperties.getLocaleResolver() == WebProperties.LocaleResolver.FIXED) {
                return new FixedLocaleResolver(this.webProperties.getLocale());
            }
            AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
            localeResolver.setDefaultLocale(this.webProperties.getLocale());
            return localeResolver;
        }

        @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
        @ConditionalOnMissingBean(name = {DispatcherServlet.THEME_RESOLVER_BEAN_NAME})
        @Bean
        @Deprecated(since = "3.0.0", forRemoval = false)
        public ThemeResolver themeResolver() {
            return super.themeResolver();
        }

        @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
        @ConditionalOnMissingBean(name = {DispatcherServlet.FLASH_MAP_MANAGER_BEAN_NAME})
        @Bean
        public FlashMapManager flashMapManager() {
            return super.flashMapManager();
        }

        private Resource getIndexHtmlResource() {
            for (String location : this.resourceProperties.getStaticLocations()) {
                Resource indexHtml = getIndexHtmlResource(location);
                if (indexHtml != null) {
                    return indexHtml;
                }
            }
            ServletContext servletContext = getServletContext();
            if (servletContext != null) {
                return getIndexHtmlResource(new ServletContextResource(servletContext, "/"));
            }
            return null;
        }

        private Resource getIndexHtmlResource(String location) {
            return getIndexHtmlResource(this.resourceLoader.getResource(location));
        }

        private Resource getIndexHtmlResource(Resource location) {
            try {
                Resource resource = location.createRelative("index.html");
                if (!resource.exists()) {
                    return null;
                }
                if (resource.getURL() != null) {
                    return resource;
                }
                return null;
            } catch (Exception e) {
                return null;
            }
        }

        @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
        @Bean
        public FormattingConversionService mvcConversionService() {
            WebMvcProperties.Format format = this.mvcProperties.getFormat();
            WebConversionService conversionService = new WebConversionService(new DateTimeFormatters().dateFormat(format.getDate()).timeFormat(format.getTime()).dateTimeFormat(format.getDateTime()));
            addFormatters(conversionService);
            return conversionService;
        }

        @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
        @Bean
        public Validator mvcValidator() {
            if (!ClassUtils.isPresent("jakarta.validation.Validator", getClass().getClassLoader())) {
                return super.mvcValidator();
            }
            return ValidatorAdapter.get(getApplicationContext(), getValidator());
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
        public RequestMappingHandlerMapping createRequestMappingHandlerMapping() {
            RequestMappingHandlerMapping mapping;
            if (this.mvcRegistrations != null && (mapping = this.mvcRegistrations.getRequestMappingHandlerMapping()) != null) {
                return mapping;
            }
            return super.createRequestMappingHandlerMapping();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
        public ConfigurableWebBindingInitializer getConfigurableWebBindingInitializer(FormattingConversionService mvcConversionService, Validator mvcValidator) {
            try {
                return (ConfigurableWebBindingInitializer) this.beanFactory.getBean(ConfigurableWebBindingInitializer.class);
            } catch (NoSuchBeanDefinitionException e) {
                return super.getConfigurableWebBindingInitializer(mvcConversionService, mvcValidator);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
        public ExceptionHandlerExceptionResolver createExceptionHandlerExceptionResolver() {
            ExceptionHandlerExceptionResolver resolver;
            if (this.mvcRegistrations != null && (resolver = this.mvcRegistrations.getExceptionHandlerExceptionResolver()) != null) {
                return resolver;
            }
            return super.createExceptionHandlerExceptionResolver();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration, org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
        public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
            super.extendHandlerExceptionResolvers(exceptionResolvers);
            if (this.mvcProperties.isLogResolvedException()) {
                for (HandlerExceptionResolver resolver : exceptionResolvers) {
                    if (resolver instanceof AbstractHandlerExceptionResolver) {
                        AbstractHandlerExceptionResolver abstractResolver = (AbstractHandlerExceptionResolver) resolver;
                        abstractResolver.setWarnLogCategory(resolver.getClass().getName());
                    }
                }
            }
        }

        @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
        @Bean
        public ContentNegotiationManager mvcContentNegotiationManager() {
            ContentNegotiationManager manager = super.mvcContentNegotiationManager();
            List<ContentNegotiationStrategy> strategies = manager.getStrategies();
            ListIterator<ContentNegotiationStrategy> iterator = strategies.listIterator();
            while (iterator.hasNext()) {
                ContentNegotiationStrategy strategy = iterator.next();
                if (strategy instanceof PathExtensionContentNegotiationStrategy) {
                    iterator.set(new OptionalPathExtensionContentNegotiationStrategy(strategy));
                }
            }
            return manager;
        }

        @Override // org.springframework.context.ResourceLoaderAware
        public void setResourceLoader(ResourceLoader resourceLoader) {
            this.resourceLoader = resourceLoader;
        }
    }

    @ConditionalOnEnabledResourceChain
    @Configuration(proxyBeanMethods = false)
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/WebMvcAutoConfiguration$ResourceChainCustomizerConfiguration.class */
    static class ResourceChainCustomizerConfiguration {
        ResourceChainCustomizerConfiguration() {
        }

        @Bean
        ResourceChainResourceHandlerRegistrationCustomizer resourceHandlerRegistrationCustomizer(WebProperties webProperties) {
            return new ResourceChainResourceHandlerRegistrationCustomizer(webProperties.getResources());
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/WebMvcAutoConfiguration$ResourceChainResourceHandlerRegistrationCustomizer.class */
    static class ResourceChainResourceHandlerRegistrationCustomizer implements ResourceHandlerRegistrationCustomizer {
        private final WebProperties.Resources resourceProperties;

        ResourceChainResourceHandlerRegistrationCustomizer(WebProperties.Resources resourceProperties) {
            this.resourceProperties = resourceProperties;
        }

        @Override // org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration.ResourceHandlerRegistrationCustomizer
        public void customize(ResourceHandlerRegistration registration) {
            WebProperties.Resources.Chain properties = this.resourceProperties.getChain();
            configureResourceChain(properties, registration.resourceChain(properties.isCache()));
        }

        private void configureResourceChain(WebProperties.Resources.Chain properties, ResourceChainRegistration chain) {
            WebProperties.Resources.Chain.Strategy strategy = properties.getStrategy();
            if (properties.isCompressed()) {
                chain.addResolver(new EncodedResourceResolver());
            }
            if (strategy.getFixed().isEnabled() || strategy.getContent().isEnabled()) {
                chain.addResolver(getVersionResourceResolver(strategy));
            }
        }

        private ResourceResolver getVersionResourceResolver(WebProperties.Resources.Chain.Strategy properties) {
            VersionResourceResolver resolver = new VersionResourceResolver();
            if (properties.getFixed().isEnabled()) {
                String version = properties.getFixed().getVersion();
                String[] paths = properties.getFixed().getPaths();
                resolver.addFixedVersionStrategy(version, paths);
            }
            if (properties.getContent().isEnabled()) {
                String[] paths2 = properties.getContent().getPaths();
                resolver.addContentVersionStrategy(paths2);
            }
            return resolver;
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = "spring.mvc.problemdetails", name = {"enabled"}, havingValue = "true")
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/WebMvcAutoConfiguration$ProblemDetailsErrorHandlingConfiguration.class */
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

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/WebMvcAutoConfiguration$OptionalPathExtensionContentNegotiationStrategy.class */
    static class OptionalPathExtensionContentNegotiationStrategy implements ContentNegotiationStrategy {
        private static final String SKIP_ATTRIBUTE = PathExtensionContentNegotiationStrategy.class.getName() + ".SKIP";
        private final ContentNegotiationStrategy delegate;

        OptionalPathExtensionContentNegotiationStrategy(ContentNegotiationStrategy delegate) {
            this.delegate = delegate;
        }

        @Override // org.springframework.web.accept.ContentNegotiationStrategy
        public List<MediaType> resolveMediaTypes(NativeWebRequest webRequest) throws HttpMediaTypeNotAcceptableException {
            Object skip = webRequest.getAttribute(SKIP_ATTRIBUTE, 0);
            if (skip != null && Boolean.parseBoolean(skip.toString())) {
                return MEDIA_TYPE_ALL_LIST;
            }
            return this.delegate.resolveMediaTypes(webRequest);
        }
    }
}
