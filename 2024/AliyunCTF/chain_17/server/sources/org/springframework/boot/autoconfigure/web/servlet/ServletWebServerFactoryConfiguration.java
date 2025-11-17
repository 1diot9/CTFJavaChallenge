package org.springframework.boot.autoconfigure.web.servlet;

import io.undertow.Undertow;
import jakarta.servlet.Servlet;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.UpgradeProtocol;
import org.eclipse.jetty.ee10.webapp.WebAppContext;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.Loader;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.embedded.jetty.JettyServerCustomizer;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.undertow.UndertowBuilderCustomizer;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xnio.SslClientAuthMode;

@Configuration(proxyBeanMethods = false)
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/ServletWebServerFactoryConfiguration.class */
class ServletWebServerFactoryConfiguration {
    ServletWebServerFactoryConfiguration() {
    }

    @ConditionalOnMissingBean(value = {ServletWebServerFactory.class}, search = SearchStrategy.CURRENT)
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({Servlet.class, Tomcat.class, UpgradeProtocol.class})
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/ServletWebServerFactoryConfiguration$EmbeddedTomcat.class */
    static class EmbeddedTomcat {
        EmbeddedTomcat() {
        }

        @Bean
        TomcatServletWebServerFactory tomcatServletWebServerFactory(ObjectProvider<TomcatConnectorCustomizer> connectorCustomizers, ObjectProvider<TomcatContextCustomizer> contextCustomizers, ObjectProvider<TomcatProtocolHandlerCustomizer<?>> protocolHandlerCustomizers) {
            TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
            factory.getTomcatConnectorCustomizers().addAll(connectorCustomizers.orderedStream().toList());
            factory.getTomcatContextCustomizers().addAll(contextCustomizers.orderedStream().toList());
            factory.getTomcatProtocolHandlerCustomizers().addAll(protocolHandlerCustomizers.orderedStream().toList());
            return factory;
        }
    }

    @ConditionalOnMissingBean(value = {ServletWebServerFactory.class}, search = SearchStrategy.CURRENT)
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({Servlet.class, Server.class, Loader.class, WebAppContext.class})
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/ServletWebServerFactoryConfiguration$EmbeddedJetty.class */
    static class EmbeddedJetty {
        EmbeddedJetty() {
        }

        @Bean
        JettyServletWebServerFactory jettyServletWebServerFactory(ObjectProvider<JettyServerCustomizer> serverCustomizers) {
            JettyServletWebServerFactory factory = new JettyServletWebServerFactory();
            factory.getServerCustomizers().addAll(serverCustomizers.orderedStream().toList());
            return factory;
        }
    }

    @ConditionalOnMissingBean(value = {ServletWebServerFactory.class}, search = SearchStrategy.CURRENT)
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({Servlet.class, Undertow.class, SslClientAuthMode.class})
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/ServletWebServerFactoryConfiguration$EmbeddedUndertow.class */
    static class EmbeddedUndertow {
        EmbeddedUndertow() {
        }

        @Bean
        UndertowServletWebServerFactory undertowServletWebServerFactory(ObjectProvider<UndertowDeploymentInfoCustomizer> deploymentInfoCustomizers, ObjectProvider<UndertowBuilderCustomizer> builderCustomizers) {
            UndertowServletWebServerFactory factory = new UndertowServletWebServerFactory();
            factory.getDeploymentInfoCustomizers().addAll(deploymentInfoCustomizers.orderedStream().toList());
            factory.getBuilderCustomizers().addAll(builderCustomizers.orderedStream().toList());
            return factory;
        }

        @Bean
        UndertowServletWebServerFactoryCustomizer undertowServletWebServerFactoryCustomizer(ServerProperties serverProperties) {
            return new UndertowServletWebServerFactoryCustomizer(serverProperties);
        }
    }
}
