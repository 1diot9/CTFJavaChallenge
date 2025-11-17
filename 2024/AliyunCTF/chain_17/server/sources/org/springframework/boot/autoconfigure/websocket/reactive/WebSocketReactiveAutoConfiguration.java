package org.springframework.boot.autoconfigure.websocket.reactive;

import jakarta.servlet.Servlet;
import jakarta.websocket.server.ServerContainer;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.websocket.server.WsSci;
import org.eclipse.jetty.ee10.websocket.jakarta.server.config.JakartaWebSocketServletContainerInitializer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.reactive.ReactiveWebServerFactoryAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AutoConfiguration(before = {ReactiveWebServerFactoryAutoConfiguration.class})
@ConditionalOnClass({Servlet.class, ServerContainer.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/websocket/reactive/WebSocketReactiveAutoConfiguration.class */
public class WebSocketReactiveAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({Tomcat.class, WsSci.class})
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/websocket/reactive/WebSocketReactiveAutoConfiguration$TomcatWebSocketConfiguration.class */
    static class TomcatWebSocketConfiguration {
        TomcatWebSocketConfiguration() {
        }

        @ConditionalOnMissingBean(name = {"websocketReactiveWebServerCustomizer"})
        @Bean
        TomcatWebSocketReactiveWebServerCustomizer websocketReactiveWebServerCustomizer() {
            return new TomcatWebSocketReactiveWebServerCustomizer();
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({JakartaWebSocketServletContainerInitializer.class})
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/websocket/reactive/WebSocketReactiveAutoConfiguration$JettyWebSocketConfiguration.class */
    static class JettyWebSocketConfiguration {
        JettyWebSocketConfiguration() {
        }

        @ConditionalOnMissingBean(name = {"websocketReactiveWebServerCustomizer"})
        @Bean
        JettyWebSocketReactiveWebServerCustomizer websocketServletWebServerCustomizer() {
            return new JettyWebSocketReactiveWebServerCustomizer();
        }
    }
}
