package org.springframework.boot.autoconfigure.web.embedded;

import io.undertow.Undertow;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.UpgradeProtocol;
import org.eclipse.jetty.ee10.webapp.WebAppContext;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.Loader;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWarDeployment;
import org.springframework.boot.autoconfigure.condition.ConditionalOnThreading;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.thread.Threading;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.xnio.SslClientAuthMode;
import reactor.netty.http.server.HttpServer;

@EnableConfigurationProperties({ServerProperties.class})
@AutoConfiguration
@ConditionalOnNotWarDeployment
@ConditionalOnWebApplication
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/embedded/EmbeddedWebServerFactoryCustomizerAutoConfiguration.class */
public class EmbeddedWebServerFactoryCustomizerAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({Tomcat.class, UpgradeProtocol.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/embedded/EmbeddedWebServerFactoryCustomizerAutoConfiguration$TomcatWebServerFactoryCustomizerConfiguration.class */
    public static class TomcatWebServerFactoryCustomizerConfiguration {
        @Bean
        public TomcatWebServerFactoryCustomizer tomcatWebServerFactoryCustomizer(Environment environment, ServerProperties serverProperties) {
            return new TomcatWebServerFactoryCustomizer(environment, serverProperties);
        }

        @ConditionalOnThreading(Threading.VIRTUAL)
        @Bean
        TomcatVirtualThreadsWebServerFactoryCustomizer tomcatVirtualThreadsProtocolHandlerCustomizer() {
            return new TomcatVirtualThreadsWebServerFactoryCustomizer();
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({Server.class, Loader.class, WebAppContext.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/embedded/EmbeddedWebServerFactoryCustomizerAutoConfiguration$JettyWebServerFactoryCustomizerConfiguration.class */
    public static class JettyWebServerFactoryCustomizerConfiguration {
        @Bean
        public JettyWebServerFactoryCustomizer jettyWebServerFactoryCustomizer(Environment environment, ServerProperties serverProperties) {
            return new JettyWebServerFactoryCustomizer(environment, serverProperties);
        }

        @ConditionalOnThreading(Threading.VIRTUAL)
        @Bean
        JettyVirtualThreadsWebServerFactoryCustomizer jettyVirtualThreadsWebServerFactoryCustomizer(ServerProperties serverProperties) {
            return new JettyVirtualThreadsWebServerFactoryCustomizer(serverProperties);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({Undertow.class, SslClientAuthMode.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/embedded/EmbeddedWebServerFactoryCustomizerAutoConfiguration$UndertowWebServerFactoryCustomizerConfiguration.class */
    public static class UndertowWebServerFactoryCustomizerConfiguration {
        @Bean
        public UndertowWebServerFactoryCustomizer undertowWebServerFactoryCustomizer(Environment environment, ServerProperties serverProperties) {
            return new UndertowWebServerFactoryCustomizer(environment, serverProperties);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({HttpServer.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/embedded/EmbeddedWebServerFactoryCustomizerAutoConfiguration$NettyWebServerFactoryCustomizerConfiguration.class */
    public static class NettyWebServerFactoryCustomizerConfiguration {
        @Bean
        public NettyWebServerFactoryCustomizer nettyWebServerFactoryCustomizer(Environment environment, ServerProperties serverProperties) {
            return new NettyWebServerFactoryCustomizer(environment, serverProperties);
        }
    }
}
