package org.springframework.boot.autoconfigure.freemarker;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.Servlet;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.ConditionalOnEnabledResourceChain;
import org.springframework.boot.autoconfigure.web.servlet.ConditionalOnMissingFilterBean;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({Servlet.class, FreeMarkerConfigurer.class})
@AutoConfigureAfter({WebMvcAutoConfiguration.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/freemarker/FreeMarkerServletWebConfiguration.class */
class FreeMarkerServletWebConfiguration extends AbstractFreeMarkerConfiguration {
    protected FreeMarkerServletWebConfiguration(FreeMarkerProperties properties) {
        super(properties);
    }

    @ConditionalOnMissingBean({FreeMarkerConfig.class})
    @Bean
    FreeMarkerConfigurer freeMarkerConfigurer() {
        FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
        applyProperties(configurer);
        return configurer;
    }

    @Bean
    freemarker.template.Configuration freeMarkerConfiguration(FreeMarkerConfig configurer) {
        return configurer.getConfiguration();
    }

    @ConditionalOnMissingBean(name = {"freeMarkerViewResolver"})
    @ConditionalOnProperty(name = {"spring.freemarker.enabled"}, matchIfMissing = true)
    @Bean
    FreeMarkerViewResolver freeMarkerViewResolver() {
        FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();
        getProperties().applyToMvcViewResolver(resolver);
        return resolver;
    }

    @ConditionalOnMissingFilterBean({ResourceUrlEncodingFilter.class})
    @ConditionalOnEnabledResourceChain
    @Bean
    FilterRegistrationBean<ResourceUrlEncodingFilter> resourceUrlEncodingFilter() {
        FilterRegistrationBean<ResourceUrlEncodingFilter> registration = new FilterRegistrationBean<>(new ResourceUrlEncodingFilter(), new ServletRegistrationBean[0]);
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ERROR);
        return registration;
    }
}
