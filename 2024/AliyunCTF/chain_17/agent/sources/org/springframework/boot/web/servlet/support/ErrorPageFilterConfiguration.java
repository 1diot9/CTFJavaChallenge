package org.springframework.boot.web.servlet.support;

import jakarta.servlet.DispatcherType;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/servlet/support/ErrorPageFilterConfiguration.class */
class ErrorPageFilterConfiguration {
    ErrorPageFilterConfiguration() {
    }

    @Bean
    ErrorPageFilter errorPageFilter() {
        return new ErrorPageFilter();
    }

    @Bean
    FilterRegistrationBean<ErrorPageFilter> errorPageFilterRegistration(ErrorPageFilter filter) {
        FilterRegistrationBean<ErrorPageFilter> registration = new FilterRegistrationBean<>(filter, new ServletRegistrationBean[0]);
        registration.setOrder(filter.getOrder());
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC);
        return registration;
    }
}
