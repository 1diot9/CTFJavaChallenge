package org.springframework.boot.autoconfigure.web.reactive.function.client;

import java.util.Objects;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.core5.http.nio.AsyncRequestProducer;
import org.apache.hc.core5.reactive.ReactiveResponseConsumer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.reactor.netty.ReactorNettyConfigurations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.ReactorResourceFactory;
import reactor.netty.http.client.HttpClient;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/function/client/ClientHttpConnectorFactoryConfiguration.class */
class ClientHttpConnectorFactoryConfiguration {
    ClientHttpConnectorFactoryConfiguration() {
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({HttpClient.class})
    @ConditionalOnMissingBean({ClientHttpConnectorFactory.class})
    @Import({ReactorNettyConfigurations.ReactorResourceFactoryConfiguration.class})
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/function/client/ClientHttpConnectorFactoryConfiguration$ReactorNetty.class */
    static class ReactorNetty {
        ReactorNetty() {
        }

        @Bean
        ReactorClientHttpConnectorFactory reactorClientHttpConnectorFactory(ReactorResourceFactory reactorResourceFactory, ObjectProvider<ReactorNettyHttpClientMapper> mapperProvider) {
            Objects.requireNonNull(mapperProvider);
            return new ReactorClientHttpConnectorFactory(reactorResourceFactory, mapperProvider::orderedStream);
        }
    }

    @ConditionalOnMissingBean({ClientHttpConnectorFactory.class})
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({HttpAsyncClients.class, AsyncRequestProducer.class, ReactiveResponseConsumer.class})
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/function/client/ClientHttpConnectorFactoryConfiguration$HttpClient5.class */
    static class HttpClient5 {
        HttpClient5() {
        }

        @Bean
        HttpComponentsClientHttpConnectorFactory httpComponentsClientHttpConnectorFactory() {
            return new HttpComponentsClientHttpConnectorFactory();
        }
    }

    @ConditionalOnMissingBean({ClientHttpConnectorFactory.class})
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({java.net.http.HttpClient.class})
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/function/client/ClientHttpConnectorFactoryConfiguration$JdkClient.class */
    static class JdkClient {
        JdkClient() {
        }

        @Bean
        JdkClientHttpConnectorFactory jdkClientHttpConnectorFactory() {
            return new JdkClientHttpConnectorFactory();
        }
    }
}
