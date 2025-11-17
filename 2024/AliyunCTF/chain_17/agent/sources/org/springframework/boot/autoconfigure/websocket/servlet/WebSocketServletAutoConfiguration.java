package org.springframework.boot.autoconfigure.websocket.servlet;

import io.undertow.websockets.jsr.Bootstrap;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.Servlet;
import jakarta.websocket.server.ServerContainer;
import java.util.EnumSet;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.websocket.server.WsSci;
import org.eclipse.jetty.ee10.websocket.jakarta.server.config.JakartaWebSocketServletContainerInitializer;
import org.eclipse.jetty.ee10.websocket.servlet.WebSocketUpgradeFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWarDeployment;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@AutoConfiguration(before = {ServletWebServerFactoryAutoConfiguration.class})
@ConditionalOnClass({Servlet.class, ServerContainer.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/websocket/servlet/WebSocketServletAutoConfiguration.class */
public class WebSocketServletAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({Tomcat.class, WsSci.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/websocket/servlet/WebSocketServletAutoConfiguration$TomcatWebSocketConfiguration.class */
    static class TomcatWebSocketConfiguration {
        TomcatWebSocketConfiguration() {
        }

        @ConditionalOnMissingBean(name = {"websocketServletWebServerCustomizer"})
        @Bean
        TomcatWebSocketServletWebServerCustomizer websocketServletWebServerCustomizer() {
            return new TomcatWebSocketServletWebServerCustomizer();
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({JakartaWebSocketServletContainerInitializer.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/websocket/servlet/WebSocketServletAutoConfiguration$JettyWebSocketConfiguration.class */
    static class JettyWebSocketConfiguration {
        JettyWebSocketConfiguration() {
        }

        @ConditionalOnMissingBean(name = {"websocketServletWebServerCustomizer"})
        @Bean
        JettyWebSocketServletWebServerCustomizer websocketServletWebServerCustomizer() {
            return new JettyWebSocketServletWebServerCustomizer();
        }

        @ConditionalOnNotWarDeployment
        @ConditionalOnMissingBean(name = {"websocketUpgradeFilterWebServerCustomizer"})
        @Bean
        @Order(Integer.MAX_VALUE)
        WebServerFactoryCustomizer<JettyServletWebServerFactory> websocketUpgradeFilterWebServerCustomizer() {
            return factory -> {
                factory.addInitializers(servletContext -> {
                    FilterRegistration.Dynamic registration = servletContext.addFilter(WebSocketUpgradeFilter.class.getName(), (Filter) new WebSocketUpgradeFilter());
                    registration.setAsyncSupported(true);
                    registration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/*");
                });
            };
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({Bootstrap.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/websocket/servlet/WebSocketServletAutoConfiguration$UndertowWebSocketConfiguration.class */
    static class UndertowWebSocketConfiguration {
        UndertowWebSocketConfiguration() {
        }

        @ConditionalOnMissingBean(name = {"websocketServletWebServerCustomizer"})
        @Bean
        UndertowWebSocketServletWebServerCustomizer websocketServletWebServerCustomizer() {
            return new UndertowWebSocketServletWebServerCustomizer();
        }
    }
}
