package org.springframework.boot.autoconfigure.data.mongo;

import com.mongodb.client.MongoClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoConnectionDetails;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.autoconfigure.mongo.PropertiesMongoConnectionDetails;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

@EnableConfigurationProperties({MongoProperties.class})
@AutoConfiguration(after = {MongoAutoConfiguration.class})
@ConditionalOnClass({MongoClient.class, MongoTemplate.class})
@Import({MongoDataConfiguration.class, MongoDatabaseFactoryConfiguration.class, MongoDatabaseFactoryDependentConfiguration.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/mongo/MongoDataAutoConfiguration.class */
public class MongoDataAutoConfiguration {
    @ConditionalOnMissingBean({MongoConnectionDetails.class})
    @Bean
    PropertiesMongoConnectionDetails mongoConnectionDetails(MongoProperties properties) {
        return new PropertiesMongoConnectionDetails(properties);
    }
}
