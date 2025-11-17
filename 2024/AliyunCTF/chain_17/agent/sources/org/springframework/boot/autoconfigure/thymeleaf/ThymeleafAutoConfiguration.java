package org.springframework.boot.autoconfigure.thymeleaf;

import com.github.mxab.thymeleaf.extras.dataattribute.dialect.DataAttributeDialect;
import jakarta.servlet.DispatcherType;
import java.util.LinkedHashMap;
import java.util.Objects;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.websocket.BasicAuthenticator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.template.TemplateLocation;
import org.springframework.boot.autoconfigure.thymeleaf.TemplateEngineConfigurations;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.boot.autoconfigure.web.ConditionalOnEnabledResourceChain;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ConditionalOnMissingFilterBean;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.util.MimeType;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;
import org.thymeleaf.spring6.ISpringWebFluxTemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.spring6.view.reactive.ThymeleafReactiveViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

@EnableConfigurationProperties({ThymeleafProperties.class})
@AutoConfiguration(after = {WebMvcAutoConfiguration.class, WebFluxAutoConfiguration.class})
@ConditionalOnClass({TemplateMode.class, SpringTemplateEngine.class})
@Import({TemplateEngineConfigurations.ReactiveTemplateEngineConfiguration.class, TemplateEngineConfigurations.DefaultTemplateEngineConfiguration.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/thymeleaf/ThymeleafAutoConfiguration.class */
public class ThymeleafAutoConfiguration {

    @ConditionalOnMissingBean(name = {"defaultTemplateResolver"})
    @Configuration(proxyBeanMethods = false)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/thymeleaf/ThymeleafAutoConfiguration$DefaultTemplateResolverConfiguration.class */
    static class DefaultTemplateResolverConfiguration {
        private static final Log logger = LogFactory.getLog((Class<?>) DefaultTemplateResolverConfiguration.class);
        private final ThymeleafProperties properties;
        private final ApplicationContext applicationContext;

        DefaultTemplateResolverConfiguration(ThymeleafProperties properties, ApplicationContext applicationContext) {
            this.properties = properties;
            this.applicationContext = applicationContext;
            checkTemplateLocationExists();
        }

        private void checkTemplateLocationExists() {
            boolean checkTemplateLocation = this.properties.isCheckTemplateLocation();
            if (checkTemplateLocation) {
                TemplateLocation location = new TemplateLocation(this.properties.getPrefix());
                if (!location.exists(this.applicationContext)) {
                    logger.warn("Cannot find template location: " + location + " (please add some templates, check your Thymeleaf configuration, or set spring.thymeleaf.check-template-location=false)");
                }
            }
        }

        @Bean
        SpringResourceTemplateResolver defaultTemplateResolver() {
            SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
            resolver.setApplicationContext(this.applicationContext);
            resolver.setPrefix(this.properties.getPrefix());
            resolver.setSuffix(this.properties.getSuffix());
            resolver.setTemplateMode(this.properties.getMode());
            if (this.properties.getEncoding() != null) {
                resolver.setCharacterEncoding(this.properties.getEncoding().name());
            }
            resolver.setCacheable(this.properties.isCache());
            Integer order = this.properties.getTemplateResolverOrder();
            if (order != null) {
                resolver.setOrder(order);
            }
            resolver.setCheckExistence(this.properties.isCheckTemplate());
            return resolver;
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(name = {"spring.thymeleaf.enabled"}, matchIfMissing = true)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/thymeleaf/ThymeleafAutoConfiguration$ThymeleafWebMvcConfiguration.class */
    static class ThymeleafWebMvcConfiguration {
        ThymeleafWebMvcConfiguration() {
        }

        @ConditionalOnMissingFilterBean({ResourceUrlEncodingFilter.class})
        @ConditionalOnEnabledResourceChain
        @Bean
        FilterRegistrationBean<ResourceUrlEncodingFilter> resourceUrlEncodingFilter() {
            FilterRegistrationBean<ResourceUrlEncodingFilter> registration = new FilterRegistrationBean<>(new ResourceUrlEncodingFilter(), new ServletRegistrationBean[0]);
            registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ERROR);
            return registration;
        }

        @Configuration(proxyBeanMethods = false)
        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/thymeleaf/ThymeleafAutoConfiguration$ThymeleafWebMvcConfiguration$ThymeleafViewResolverConfiguration.class */
        static class ThymeleafViewResolverConfiguration {
            ThymeleafViewResolverConfiguration() {
            }

            @ConditionalOnMissingBean(name = {"thymeleafViewResolver"})
            @Bean
            ThymeleafViewResolver thymeleafViewResolver(ThymeleafProperties properties, SpringTemplateEngine templateEngine) {
                ThymeleafViewResolver resolver = new ThymeleafViewResolver();
                resolver.setTemplateEngine(templateEngine);
                resolver.setCharacterEncoding(properties.getEncoding().name());
                resolver.setContentType(appendCharset(properties.getServlet().getContentType(), resolver.getCharacterEncoding()));
                resolver.setProducePartialOutputWhileProcessing(properties.getServlet().isProducePartialOutputWhileProcessing());
                resolver.setExcludedViewNames(properties.getExcludedViewNames());
                resolver.setViewNames(properties.getViewNames());
                resolver.setOrder(SecurityProperties.BASIC_AUTH_ORDER);
                resolver.setCache(properties.isCache());
                return resolver;
            }

            private String appendCharset(MimeType type, String charset) {
                if (type.getCharset() != null) {
                    return type.toString();
                }
                LinkedHashMap<String, String> parameters = new LinkedHashMap<>();
                parameters.put(BasicAuthenticator.charsetparam, charset);
                parameters.putAll(type.getParameters());
                return new MimeType(type, parameters).toString();
            }
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(name = {"spring.thymeleaf.enabled"}, matchIfMissing = true)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/thymeleaf/ThymeleafAutoConfiguration$ThymeleafWebFluxConfiguration.class */
    static class ThymeleafWebFluxConfiguration {
        ThymeleafWebFluxConfiguration() {
        }

        @ConditionalOnMissingBean(name = {"thymeleafReactiveViewResolver"})
        @Bean
        ThymeleafReactiveViewResolver thymeleafViewResolver(ISpringWebFluxTemplateEngine templateEngine, ThymeleafProperties properties) {
            ThymeleafReactiveViewResolver resolver = new ThymeleafReactiveViewResolver();
            resolver.setTemplateEngine(templateEngine);
            mapProperties(properties, resolver);
            mapReactiveProperties(properties.getReactive(), resolver);
            resolver.setOrder(SecurityProperties.BASIC_AUTH_ORDER);
            return resolver;
        }

        private void mapProperties(ThymeleafProperties properties, ThymeleafReactiveViewResolver resolver) {
            PropertyMapper map = PropertyMapper.get();
            Objects.requireNonNull(properties);
            PropertyMapper.Source from = map.from(properties::getEncoding);
            Objects.requireNonNull(resolver);
            from.to(resolver::setDefaultCharset);
            resolver.setExcludedViewNames(properties.getExcludedViewNames());
            resolver.setViewNames(properties.getViewNames());
        }

        private void mapReactiveProperties(ThymeleafProperties.Reactive properties, ThymeleafReactiveViewResolver resolver) {
            PropertyMapper map = PropertyMapper.get();
            Objects.requireNonNull(properties);
            PropertyMapper.Source whenNonNull = map.from(properties::getMediaTypes).whenNonNull();
            Objects.requireNonNull(resolver);
            whenNonNull.to(resolver::setSupportedMediaTypes);
            Objects.requireNonNull(properties);
            PropertyMapper.Source<Integer> when = map.from(properties::getMaxChunkSize).asInt((v0) -> {
                return v0.toBytes();
            }).when(size -> {
                return size.intValue() > 0;
            });
            Objects.requireNonNull(resolver);
            when.to((v1) -> {
                r1.setResponseMaxChunkSizeBytes(v1);
            });
            Objects.requireNonNull(properties);
            PropertyMapper.Source from = map.from(properties::getFullModeViewNames);
            Objects.requireNonNull(resolver);
            from.to(resolver::setFullModeViewNames);
            Objects.requireNonNull(properties);
            PropertyMapper.Source from2 = map.from(properties::getChunkedModeViewNames);
            Objects.requireNonNull(resolver);
            from2.to(resolver::setChunkedModeViewNames);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({LayoutDialect.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/thymeleaf/ThymeleafAutoConfiguration$ThymeleafWebLayoutConfiguration.class */
    static class ThymeleafWebLayoutConfiguration {
        ThymeleafWebLayoutConfiguration() {
        }

        @ConditionalOnMissingBean
        @Bean
        LayoutDialect layoutDialect() {
            return new LayoutDialect();
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({DataAttributeDialect.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/thymeleaf/ThymeleafAutoConfiguration$DataAttributeDialectConfiguration.class */
    static class DataAttributeDialectConfiguration {
        DataAttributeDialectConfiguration() {
        }

        @ConditionalOnMissingBean
        @Bean
        DataAttributeDialect dialect() {
            return new DataAttributeDialect();
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({SpringSecurityDialect.class, CsrfToken.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/thymeleaf/ThymeleafAutoConfiguration$ThymeleafSecurityDialectConfiguration.class */
    static class ThymeleafSecurityDialectConfiguration {
        ThymeleafSecurityDialectConfiguration() {
        }

        @ConditionalOnMissingBean
        @Bean
        SpringSecurityDialect securityDialect() {
            return new SpringSecurityDialect();
        }
    }
}
