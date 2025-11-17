package org.springframework.web.servlet.config.annotation;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration(proxyBeanMethods = false)
/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/config/annotation/DelegatingWebMvcConfiguration.class */
public class DelegatingWebMvcConfiguration extends WebMvcConfigurationSupport {
    private final WebMvcConfigurerComposite configurers = new WebMvcConfigurerComposite();

    @Autowired(required = false)
    public void setConfigurers(List<WebMvcConfigurer> configurers) {
        if (!CollectionUtils.isEmpty(configurers)) {
            this.configurers.addWebMvcConfigurers(configurers);
        }
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
    protected void configurePathMatch(PathMatchConfigurer configurer) {
        this.configurers.configurePathMatch(configurer);
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
    protected void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        this.configurers.configureContentNegotiation(configurer);
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
    protected void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        this.configurers.configureAsyncSupport(configurer);
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
    protected void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        this.configurers.configureDefaultServletHandling(configurer);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
    public void addFormatters(FormatterRegistry registry) {
        this.configurers.addFormatters(registry);
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
    protected void addInterceptors(InterceptorRegistry registry) {
        this.configurers.addInterceptors(registry);
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        this.configurers.addResourceHandlers(registry);
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
    protected void addCorsMappings(CorsRegistry registry) {
        this.configurers.addCorsMappings(registry);
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
    protected void addViewControllers(ViewControllerRegistry registry) {
        this.configurers.addViewControllers(registry);
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
    protected void configureViewResolvers(ViewResolverRegistry registry) {
        this.configurers.configureViewResolvers(registry);
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
    protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        this.configurers.addArgumentResolvers(argumentResolvers);
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
    protected void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        this.configurers.addReturnValueHandlers(returnValueHandlers);
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        this.configurers.configureMessageConverters(converters);
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        this.configurers.extendMessageConverters(converters);
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
    protected void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        this.configurers.configureHandlerExceptionResolvers(exceptionResolvers);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        this.configurers.extendHandlerExceptionResolvers(exceptionResolvers);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
    @Nullable
    public Validator getValidator() {
        return this.configurers.getValidator();
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
    @Nullable
    protected MessageCodesResolver getMessageCodesResolver() {
        return this.configurers.getMessageCodesResolver();
    }
}
