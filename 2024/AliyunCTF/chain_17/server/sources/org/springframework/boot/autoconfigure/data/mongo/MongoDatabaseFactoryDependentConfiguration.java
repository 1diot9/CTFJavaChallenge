package org.springframework.boot.autoconfigure.data.mongo;

import com.mongodb.ClientSessionOptions;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoDatabase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.mongo.MongoConnectionDetails;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Configuration(proxyBeanMethods = false)
@ConditionalOnBean({MongoDatabaseFactory.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/mongo/MongoDatabaseFactoryDependentConfiguration.class */
class MongoDatabaseFactoryDependentConfiguration {
    MongoDatabaseFactoryDependentConfiguration() {
    }

    @ConditionalOnMissingBean({MongoOperations.class})
    @Bean
    MongoTemplate mongoTemplate(MongoDatabaseFactory factory, MongoConverter converter) {
        return new MongoTemplate(factory, converter);
    }

    @ConditionalOnMissingBean({MongoConverter.class})
    @Bean
    MappingMongoConverter mappingMongoConverter(MongoDatabaseFactory factory, MongoMappingContext context, MongoCustomConversions conversions) {
        MappingMongoConverter mappingConverter = new MappingMongoConverter(new DefaultDbRefResolver(factory), context);
        mappingConverter.setCustomConversions(conversions);
        return mappingConverter;
    }

    @ConditionalOnMissingBean({GridFsOperations.class})
    @Bean
    GridFsTemplate gridFsTemplate(MongoProperties properties, MongoDatabaseFactory factory, MongoTemplate mongoTemplate, MongoConnectionDetails connectionDetails) {
        return new GridFsTemplate(new GridFsMongoDatabaseFactory(factory, connectionDetails), mongoTemplate.getConverter(), connectionDetails.getGridFs() != null ? connectionDetails.getGridFs().getBucket() : null);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/mongo/MongoDatabaseFactoryDependentConfiguration$GridFsMongoDatabaseFactory.class */
    static class GridFsMongoDatabaseFactory implements MongoDatabaseFactory {
        private final MongoDatabaseFactory mongoDatabaseFactory;
        private final MongoConnectionDetails connectionDetails;

        GridFsMongoDatabaseFactory(MongoDatabaseFactory mongoDatabaseFactory, MongoConnectionDetails connectionDetails) {
            Assert.notNull(mongoDatabaseFactory, "MongoDatabaseFactory must not be null");
            Assert.notNull(connectionDetails, "ConnectionDetails must not be null");
            this.mongoDatabaseFactory = mongoDatabaseFactory;
            this.connectionDetails = connectionDetails;
        }

        public MongoDatabase getMongoDatabase() throws DataAccessException {
            String gridFsDatabase = getGridFsDatabase(this.connectionDetails);
            if (StringUtils.hasText(gridFsDatabase)) {
                return this.mongoDatabaseFactory.getMongoDatabase(gridFsDatabase);
            }
            return this.mongoDatabaseFactory.getMongoDatabase();
        }

        public MongoDatabase getMongoDatabase(String dbName) throws DataAccessException {
            return this.mongoDatabaseFactory.getMongoDatabase(dbName);
        }

        public PersistenceExceptionTranslator getExceptionTranslator() {
            return this.mongoDatabaseFactory.getExceptionTranslator();
        }

        public ClientSession getSession(ClientSessionOptions options) {
            return this.mongoDatabaseFactory.getSession(options);
        }

        public MongoDatabaseFactory withSession(ClientSession session) {
            return this.mongoDatabaseFactory.withSession(session);
        }

        private String getGridFsDatabase(MongoConnectionDetails connectionDetails) {
            if (connectionDetails.getGridFs() != null) {
                return connectionDetails.getGridFs().getDatabase();
            }
            return null;
        }
    }
}
