package org.springframework.boot.autoconfigure.data.mongo;

import com.mongodb.client.MongoClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.mongo.MongoConnectionDetails;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoDatabaseFactorySupport;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@ConditionalOnMissingBean({MongoDatabaseFactory.class})
@Configuration(proxyBeanMethods = false)
@ConditionalOnSingleCandidate(MongoClient.class)
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/mongo/MongoDatabaseFactoryConfiguration.class */
class MongoDatabaseFactoryConfiguration {
    MongoDatabaseFactoryConfiguration() {
    }

    @Bean
    MongoDatabaseFactorySupport<?> mongoDatabaseFactory(MongoClient mongoClient, MongoProperties properties, MongoConnectionDetails connectionDetails) {
        String database = properties.getDatabase();
        if (database == null) {
            database = connectionDetails.getConnectionString().getDatabase();
        }
        return new SimpleMongoClientDatabaseFactory(mongoClient, database);
    }
}
