package org.springframework.boot.autoconfigure.session;

import jakarta.servlet.DispatcherType;
import java.util.EnumSet;
import java.util.stream.Collectors;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.session.SessionProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.DelegatingFilterProxyRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.SessionRepositoryFilter;
import org.springframework.util.Assert;

@EnableConfigurationProperties({SessionProperties.class})
@Configuration(proxyBeanMethods = false)
@ConditionalOnBean({SessionRepositoryFilter.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/session/SessionRepositoryFilterConfiguration.class */
class SessionRepositoryFilterConfiguration {
    SessionRepositoryFilterConfiguration() {
    }

    @Bean
    DelegatingFilterProxyRegistrationBean sessionRepositoryFilterRegistration(SessionProperties sessionProperties, ListableBeanFactory beanFactory) {
        String[] targetBeanNames = beanFactory.getBeanNamesForType(SessionRepositoryFilter.class, false, false);
        Assert.state(targetBeanNames.length == 1, "Expected single SessionRepositoryFilter bean");
        DelegatingFilterProxyRegistrationBean registration = new DelegatingFilterProxyRegistrationBean(targetBeanNames[0], new ServletRegistrationBean[0]);
        registration.setDispatcherTypes(getDispatcherTypes(sessionProperties));
        registration.setOrder(sessionProperties.getServlet().getFilterOrder());
        return registration;
    }

    private EnumSet<DispatcherType> getDispatcherTypes(SessionProperties sessionProperties) {
        SessionProperties.Servlet servletProperties = sessionProperties.getServlet();
        if (servletProperties.getFilterDispatcherTypes() == null) {
            return null;
        }
        return (EnumSet) servletProperties.getFilterDispatcherTypes().stream().map(type -> {
            return DispatcherType.valueOf(type.name());
        }).collect(Collectors.toCollection(() -> {
            return EnumSet.noneOf(DispatcherType.class);
        }));
    }
}
