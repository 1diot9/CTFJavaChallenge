package org.springframework.boot.autoconfigure.mongo;

import com.mongodb.MongoClientSettings;
import com.mongodb.connection.TransportSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import reactor.core.publisher.Flux;

@EnableConfigurationProperties({MongoProperties.class})
@AutoConfiguration
@ConditionalOnClass({MongoClient.class, Flux.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/mongo/MongoReactiveAutoConfiguration.class */
public class MongoReactiveAutoConfiguration {
    @ConditionalOnMissingBean({MongoConnectionDetails.class})
    @Bean
    PropertiesMongoConnectionDetails mongoConnectionDetails(MongoProperties properties) {
        return new PropertiesMongoConnectionDetails(properties);
    }

    @ConditionalOnMissingBean
    @Bean
    public MongoClient reactiveStreamsMongoClient(ObjectProvider<MongoClientSettingsBuilderCustomizer> builderCustomizers, MongoClientSettings settings) {
        ReactiveMongoClientFactory factory = new ReactiveMongoClientFactory(builderCustomizers.orderedStream().toList());
        return factory.createMongoClient(settings);
    }

    @ConditionalOnMissingBean({MongoClientSettings.class})
    @Configuration(proxyBeanMethods = false)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/mongo/MongoReactiveAutoConfiguration$MongoClientSettingsConfiguration.class */
    static class MongoClientSettingsConfiguration {
        MongoClientSettingsConfiguration() {
        }

        @Bean
        MongoClientSettings mongoClientSettings() {
            return MongoClientSettings.builder().build();
        }

        @Bean
        StandardMongoClientSettingsBuilderCustomizer standardMongoSettingsCustomizer(MongoProperties properties, MongoConnectionDetails connectionDetails, ObjectProvider<SslBundles> sslBundles) {
            return new StandardMongoClientSettingsBuilderCustomizer(connectionDetails.getConnectionString(), properties.getUuidRepresentation(), properties.getSsl(), sslBundles.getIfAvailable());
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({SocketChannel.class, NioEventLoopGroup.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/mongo/MongoReactiveAutoConfiguration$NettyDriverConfiguration.class */
    static class NettyDriverConfiguration {
        NettyDriverConfiguration() {
        }

        @Bean
        @Order(Integer.MIN_VALUE)
        NettyDriverMongoClientSettingsBuilderCustomizer nettyDriverCustomizer(ObjectProvider<MongoClientSettings> settings) {
            return new NettyDriverMongoClientSettingsBuilderCustomizer(settings);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/mongo/MongoReactiveAutoConfiguration$NettyDriverMongoClientSettingsBuilderCustomizer.class */
    static final class NettyDriverMongoClientSettingsBuilderCustomizer implements MongoClientSettingsBuilderCustomizer, DisposableBean {
        private final ObjectProvider<MongoClientSettings> settings;
        private volatile EventLoopGroup eventLoopGroup;

        NettyDriverMongoClientSettingsBuilderCustomizer(ObjectProvider<MongoClientSettings> settings) {
            this.settings = settings;
        }

        @Override // org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer
        public void customize(MongoClientSettings.Builder builder) {
            if (!isCustomTransportConfiguration(this.settings.getIfAvailable())) {
                NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
                this.eventLoopGroup = eventLoopGroup;
                builder.transportSettings(TransportSettings.nettyBuilder().eventLoopGroup(eventLoopGroup).build());
            }
        }

        @Override // org.springframework.beans.factory.DisposableBean
        public void destroy() {
            EventLoopGroup eventLoopGroup = this.eventLoopGroup;
            if (eventLoopGroup != null) {
                eventLoopGroup.shutdownGracefully().awaitUninterruptibly();
                this.eventLoopGroup = null;
            }
        }

        private boolean isCustomTransportConfiguration(MongoClientSettings settings) {
            return (settings == null || (settings.getTransportSettings() == null && settings.getStreamFactoryFactory() == null)) ? false : true;
        }
    }
}
