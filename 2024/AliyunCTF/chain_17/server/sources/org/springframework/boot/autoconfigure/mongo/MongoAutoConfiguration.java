package org.springframework.boot.autoconfigure.mongo;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties({MongoProperties.class})
@AutoConfiguration
@ConditionalOnClass({MongoClient.class})
@ConditionalOnMissingBean(type = {"org.springframework.data.mongodb.MongoDatabaseFactory"})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/mongo/MongoAutoConfiguration.class */
public class MongoAutoConfiguration {
    @ConditionalOnMissingBean({MongoConnectionDetails.class})
    @Bean
    PropertiesMongoConnectionDetails mongoConnectionDetails(MongoProperties properties) {
        return new PropertiesMongoConnectionDetails(properties);
    }

    @ConditionalOnMissingBean({MongoClient.class})
    @Bean
    public MongoClient mongo(ObjectProvider<MongoClientSettingsBuilderCustomizer> builderCustomizers, MongoClientSettings settings) {
        return new MongoClientFactory(builderCustomizers.orderedStream().toList()).createMongoClient(settings);
    }

    @ConditionalOnMissingBean({MongoClientSettings.class})
    @Configuration(proxyBeanMethods = false)
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/mongo/MongoAutoConfiguration$MongoClientSettingsConfiguration.class */
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
}
