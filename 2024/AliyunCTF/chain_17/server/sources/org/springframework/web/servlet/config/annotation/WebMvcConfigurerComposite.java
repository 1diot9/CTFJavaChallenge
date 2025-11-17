package org.springframework.web.servlet.config.annotation;

import java.util.ArrayList;
import java.util.List;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/config/annotation/WebMvcConfigurerComposite.class */
class WebMvcConfigurerComposite implements WebMvcConfigurer {
    private final List<WebMvcConfigurer> delegates = new ArrayList();

    public void addWebMvcConfigurers(List<WebMvcConfigurer> configurers) {
        if (!CollectionUtils.isEmpty(configurers)) {
            this.delegates.addAll(configurers);
        }
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
    public void configurePathMatch(PathMatchConfigurer configurer) {
        for (WebMvcConfigurer delegate : this.delegates) {
            delegate.configurePathMatch(configurer);
        }
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        for (WebMvcConfigurer delegate : this.delegates) {
            delegate.configureContentNegotiation(configurer);
        }
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        for (WebMvcConfigurer delegate : this.delegates) {
            delegate.configureAsyncSupport(configurer);
        }
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        for (WebMvcConfigurer delegate : this.delegates) {
            delegate.configureDefaultServletHandling(configurer);
        }
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
    public void addFormatters(FormatterRegistry registry) {
        for (WebMvcConfigurer delegate : this.delegates) {
            delegate.addFormatters(registry);
        }
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
    public void addInterceptors(InterceptorRegistry registry) {
        for (WebMvcConfigurer delegate : this.delegates) {
            delegate.addInterceptors(registry);
        }
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        for (WebMvcConfigurer delegate : this.delegates) {
            delegate.addResourceHandlers(registry);
        }
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
    public void addCorsMappings(CorsRegistry registry) {
        for (WebMvcConfigurer delegate : this.delegates) {
            delegate.addCorsMappings(registry);
        }
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
    public void addViewControllers(ViewControllerRegistry registry) {
        for (WebMvcConfigurer delegate : this.delegates) {
            delegate.addViewControllers(registry);
        }
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
    public void configureViewResolvers(ViewResolverRegistry registry) {
        for (WebMvcConfigurer delegate : this.delegates) {
            delegate.configureViewResolvers(registry);
        }
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        for (WebMvcConfigurer delegate : this.delegates) {
            delegate.addArgumentResolvers(argumentResolvers);
        }
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        for (WebMvcConfigurer delegate : this.delegates) {
            delegate.addReturnValueHandlers(returnValueHandlers);
        }
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (WebMvcConfigurer delegate : this.delegates) {
            delegate.configureMessageConverters(converters);
        }
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (WebMvcConfigurer delegate : this.delegates) {
            delegate.extendMessageConverters(converters);
        }
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        for (WebMvcConfigurer delegate : this.delegates) {
            delegate.configureHandlerExceptionResolvers(exceptionResolvers);
        }
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        for (WebMvcConfigurer delegate : this.delegates) {
            delegate.extendHandlerExceptionResolvers(exceptionResolvers);
        }
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
    public Validator getValidator() {
        Validator selected = null;
        for (WebMvcConfigurer configurer : this.delegates) {
            Validator validator = configurer.getValidator();
            if (validator != null) {
                if (selected != null) {
                    throw new IllegalStateException("No unique Validator found: {" + selected + ", " + validator + "}");
                }
                selected = validator;
            }
        }
        return selected;
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
    @Nullable
    public MessageCodesResolver getMessageCodesResolver() {
        MessageCodesResolver selected = null;
        for (WebMvcConfigurer configurer : this.delegates) {
            MessageCodesResolver messageCodesResolver = configurer.getMessageCodesResolver();
            if (messageCodesResolver != null) {
                if (selected != null) {
                    throw new IllegalStateException("No unique MessageCodesResolver found: {" + selected + ", " + messageCodesResolver + "}");
                }
                selected = messageCodesResolver;
            }
        }
        return selected;
    }
}
