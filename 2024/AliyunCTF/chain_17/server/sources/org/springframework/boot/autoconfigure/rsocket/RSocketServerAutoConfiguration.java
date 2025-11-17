package org.springframework.boot.autoconfigure.rsocket;

import io.rsocket.core.RSocketServer;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.server.TcpServerTransport;
import java.util.Objects;
import java.util.function.Consumer;
import org.apache.tomcat.websocket.Constants;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.reactor.netty.ReactorNettyConfigurations;
import org.springframework.boot.autoconfigure.rsocket.RSocketProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.rsocket.context.RSocketServerBootstrap;
import org.springframework.boot.rsocket.netty.NettyRSocketServerFactory;
import org.springframework.boot.rsocket.server.RSocketServerCustomizer;
import org.springframework.boot.rsocket.server.RSocketServerFactory;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.client.ReactorResourceFactory;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.WebsocketServerSpec;

@EnableConfigurationProperties({RSocketProperties.class})
@AutoConfiguration(after = {RSocketStrategiesAutoConfiguration.class})
@ConditionalOnClass({RSocketServer.class, RSocketStrategies.class, HttpServer.class, TcpServerTransport.class})
@ConditionalOnBean({RSocketMessageHandler.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/rsocket/RSocketServerAutoConfiguration.class */
public class RSocketServerAutoConfiguration {

    @Conditional({OnRSocketWebServerCondition.class})
    @Configuration(proxyBeanMethods = false)
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/rsocket/RSocketServerAutoConfiguration$WebFluxServerConfiguration.class */
    static class WebFluxServerConfiguration {
        WebFluxServerConfiguration() {
        }

        @ConditionalOnMissingBean
        @Bean
        RSocketWebSocketNettyRouteProvider rSocketWebsocketRouteProvider(RSocketProperties properties, RSocketMessageHandler messageHandler, ObjectProvider<RSocketServerCustomizer> customizers) {
            return new RSocketWebSocketNettyRouteProvider(properties.getServer().getMappingPath(), messageHandler.responder(), customizeWebsocketServerSpec(properties.getServer().getSpec()), customizers.orderedStream());
        }

        private Consumer<WebsocketServerSpec.Builder> customizeWebsocketServerSpec(RSocketProperties.Server.Spec spec) {
            return builder -> {
                PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
                PropertyMapper.Source from = map.from((PropertyMapper) spec.getProtocols());
                Objects.requireNonNull(builder);
                from.to(builder::protocols);
                PropertyMapper.Source<Integer> asInt = map.from((PropertyMapper) spec.getMaxFramePayloadLength()).asInt((v0) -> {
                    return v0.toBytes();
                });
                Objects.requireNonNull(builder);
                asInt.to((v1) -> {
                    r1.maxFramePayloadLength(v1);
                });
                PropertyMapper.Source from2 = map.from((PropertyMapper) Boolean.valueOf(spec.isHandlePing()));
                Objects.requireNonNull(builder);
                from2.to((v1) -> {
                    r1.handlePing(v1);
                });
                PropertyMapper.Source from3 = map.from((PropertyMapper) Boolean.valueOf(spec.isCompress()));
                Objects.requireNonNull(builder);
                from3.to((v1) -> {
                    r1.compress(v1);
                });
            };
        }
    }

    @ConditionalOnClass({ReactorResourceFactory.class})
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = "spring.rsocket.server", name = {"port"})
    @Import({ReactorNettyConfigurations.ReactorResourceFactoryConfiguration.class})
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/rsocket/RSocketServerAutoConfiguration$EmbeddedServerConfiguration.class */
    static class EmbeddedServerConfiguration {
        EmbeddedServerConfiguration() {
        }

        @ConditionalOnMissingBean
        @Bean
        RSocketServerFactory rSocketServerFactory(RSocketProperties properties, ReactorResourceFactory resourceFactory, ObjectProvider<RSocketServerCustomizer> customizers, ObjectProvider<SslBundles> sslBundles) {
            NettyRSocketServerFactory factory = new NettyRSocketServerFactory();
            factory.setResourceFactory(resourceFactory);
            factory.setTransport(properties.getServer().getTransport());
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            PropertyMapper.Source from = map.from((PropertyMapper) properties.getServer().getAddress());
            Objects.requireNonNull(factory);
            from.to(factory::setAddress);
            PropertyMapper.Source from2 = map.from((PropertyMapper) properties.getServer().getPort());
            Objects.requireNonNull(factory);
            from2.to((v1) -> {
                r1.setPort(v1);
            });
            PropertyMapper.Source from3 = map.from((PropertyMapper) properties.getServer().getFragmentSize());
            Objects.requireNonNull(factory);
            from3.to(factory::setFragmentSize);
            PropertyMapper.Source from4 = map.from((PropertyMapper) properties.getServer().getSsl());
            Objects.requireNonNull(factory);
            from4.to(factory::setSsl);
            factory.setSslBundles(sslBundles.getIfAvailable());
            factory.setRSocketServerCustomizers(customizers.orderedStream().toList());
            return factory;
        }

        @ConditionalOnMissingBean
        @Bean
        RSocketServerBootstrap rSocketServerBootstrap(RSocketServerFactory rSocketServerFactory, RSocketMessageHandler rSocketMessageHandler) {
            return new RSocketServerBootstrap(rSocketServerFactory, rSocketMessageHandler.responder());
        }

        @Bean
        RSocketServerCustomizer frameDecoderRSocketServerCustomizer(RSocketMessageHandler rSocketMessageHandler) {
            return server -> {
                if (rSocketMessageHandler.getRSocketStrategies().dataBufferFactory() instanceof NettyDataBufferFactory) {
                    server.payloadDecoder(PayloadDecoder.ZERO_COPY);
                }
            };
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/rsocket/RSocketServerAutoConfiguration$OnRSocketWebServerCondition.class */
    static class OnRSocketWebServerCondition extends AllNestedConditions {
        OnRSocketWebServerCondition() {
            super(ConfigurationCondition.ConfigurationPhase.PARSE_CONFIGURATION);
        }

        @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
        /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/rsocket/RSocketServerAutoConfiguration$OnRSocketWebServerCondition$IsReactiveWebApplication.class */
        static class IsReactiveWebApplication {
            IsReactiveWebApplication() {
            }
        }

        @ConditionalOnProperty(prefix = "spring.rsocket.server", name = {"port"}, matchIfMissing = true)
        /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/rsocket/RSocketServerAutoConfiguration$OnRSocketWebServerCondition$HasNoPortConfigured.class */
        static class HasNoPortConfigured {
            HasNoPortConfigured() {
            }
        }

        @ConditionalOnProperty(prefix = "spring.rsocket.server", name = {"mapping-path"})
        /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/rsocket/RSocketServerAutoConfiguration$OnRSocketWebServerCondition$HasMappingPathConfigured.class */
        static class HasMappingPathConfigured {
            HasMappingPathConfigured() {
            }
        }

        @ConditionalOnProperty(prefix = "spring.rsocket.server", name = {"transport"}, havingValue = Constants.UPGRADE_HEADER_VALUE)
        /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/rsocket/RSocketServerAutoConfiguration$OnRSocketWebServerCondition$HasWebsocketTransportConfigured.class */
        static class HasWebsocketTransportConfigured {
            HasWebsocketTransportConfigured() {
            }
        }
    }
}
