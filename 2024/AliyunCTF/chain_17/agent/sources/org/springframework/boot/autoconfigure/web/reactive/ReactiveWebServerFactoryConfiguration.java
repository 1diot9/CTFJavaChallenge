package org.springframework.boot.autoconfigure.web.reactive;

import io.undertow.Undertow;
import java.util.Objects;
import java.util.stream.Stream;
import org.apache.catalina.startup.Tomcat;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.reactor.netty.ReactorNettyConfigurations;
import org.springframework.boot.web.embedded.jetty.JettyReactiveWebServerFactory;
import org.springframework.boot.web.embedded.jetty.JettyServerCustomizer;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.embedded.netty.NettyRouteProvider;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatReactiveWebServerFactory;
import org.springframework.boot.web.embedded.undertow.UndertowBuilderCustomizer;
import org.springframework.boot.web.embedded.undertow.UndertowReactiveWebServerFactory;
import org.springframework.boot.web.reactive.server.ReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.ReactorResourceFactory;
import reactor.netty.http.server.HttpServer;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/ReactiveWebServerFactoryConfiguration.class */
abstract class ReactiveWebServerFactoryConfiguration {
    ReactiveWebServerFactoryConfiguration() {
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({HttpServer.class})
    @ConditionalOnMissingBean({ReactiveWebServerFactory.class})
    @Import({ReactorNettyConfigurations.ReactorResourceFactoryConfiguration.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/ReactiveWebServerFactoryConfiguration$EmbeddedNetty.class */
    static class EmbeddedNetty {
        EmbeddedNetty() {
        }

        @Bean
        NettyReactiveWebServerFactory nettyReactiveWebServerFactory(ReactorResourceFactory resourceFactory, ObjectProvider<NettyRouteProvider> routes, ObjectProvider<NettyServerCustomizer> serverCustomizers) {
            NettyReactiveWebServerFactory serverFactory = new NettyReactiveWebServerFactory();
            serverFactory.setResourceFactory(resourceFactory);
            Stream<NettyRouteProvider> orderedStream = routes.orderedStream();
            Objects.requireNonNull(serverFactory);
            orderedStream.forEach(xva$0 -> {
                serverFactory.addRouteProviders(xva$0);
            });
            serverFactory.getServerCustomizers().addAll(serverCustomizers.orderedStream().toList());
            return serverFactory;
        }
    }

    @ConditionalOnMissingBean({ReactiveWebServerFactory.class})
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({Tomcat.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/ReactiveWebServerFactoryConfiguration$EmbeddedTomcat.class */
    static class EmbeddedTomcat {
        EmbeddedTomcat() {
        }

        @Bean
        TomcatReactiveWebServerFactory tomcatReactiveWebServerFactory(ObjectProvider<TomcatConnectorCustomizer> connectorCustomizers, ObjectProvider<TomcatContextCustomizer> contextCustomizers, ObjectProvider<TomcatProtocolHandlerCustomizer<?>> protocolHandlerCustomizers) {
            TomcatReactiveWebServerFactory factory = new TomcatReactiveWebServerFactory();
            factory.getTomcatConnectorCustomizers().addAll(connectorCustomizers.orderedStream().toList());
            factory.getTomcatContextCustomizers().addAll(contextCustomizers.orderedStream().toList());
            factory.getTomcatProtocolHandlerCustomizers().addAll(protocolHandlerCustomizers.orderedStream().toList());
            return factory;
        }
    }

    @ConditionalOnMissingBean({ReactiveWebServerFactory.class})
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({Server.class, ServletHolder.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/ReactiveWebServerFactoryConfiguration$EmbeddedJetty.class */
    static class EmbeddedJetty {
        EmbeddedJetty() {
        }

        @Bean
        JettyReactiveWebServerFactory jettyReactiveWebServerFactory(ObjectProvider<JettyServerCustomizer> serverCustomizers) {
            JettyReactiveWebServerFactory serverFactory = new JettyReactiveWebServerFactory();
            serverFactory.getServerCustomizers().addAll(serverCustomizers.orderedStream().toList());
            return serverFactory;
        }
    }

    @ConditionalOnMissingBean({ReactiveWebServerFactory.class})
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({Undertow.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/ReactiveWebServerFactoryConfiguration$EmbeddedUndertow.class */
    static class EmbeddedUndertow {
        EmbeddedUndertow() {
        }

        @Bean
        UndertowReactiveWebServerFactory undertowReactiveWebServerFactory(ObjectProvider<UndertowBuilderCustomizer> builderCustomizers) {
            UndertowReactiveWebServerFactory factory = new UndertowReactiveWebServerFactory();
            factory.getBuilderCustomizers().addAll(builderCustomizers.orderedStream().toList());
            return factory;
        }
    }
}
