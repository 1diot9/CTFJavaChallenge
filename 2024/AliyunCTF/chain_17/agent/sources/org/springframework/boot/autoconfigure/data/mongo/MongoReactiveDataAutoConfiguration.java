package org.springframework.boot.autoconfigure.data.mongo;

import com.mongodb.ClientSessionOptions;
import com.mongodb.reactivestreams.client.ClientSession;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoDatabase;
import java.util.Optional;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.mongo.MongoConnectionDetails;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.convert.NoOpDbRefResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsOperations;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@EnableConfigurationProperties({MongoProperties.class})
@AutoConfiguration(after = {MongoReactiveAutoConfiguration.class})
@ConditionalOnClass({MongoClient.class, ReactiveMongoTemplate.class})
@ConditionalOnBean({MongoClient.class})
@Import({MongoDataConfiguration.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/mongo/MongoReactiveDataAutoConfiguration.class */
public class MongoReactiveDataAutoConfiguration {
    private final MongoConnectionDetails connectionDetails;

    MongoReactiveDataAutoConfiguration(MongoConnectionDetails connectionDetails) {
        this.connectionDetails = connectionDetails;
    }

    @ConditionalOnMissingBean({ReactiveMongoDatabaseFactory.class})
    @Bean
    public SimpleReactiveMongoDatabaseFactory reactiveMongoDatabaseFactory(MongoClient mongo, MongoProperties properties) {
        String database = properties.getDatabase();
        if (database == null) {
            database = this.connectionDetails.getConnectionString().getDatabase();
        }
        return new SimpleReactiveMongoDatabaseFactory(mongo, database);
    }

    @ConditionalOnMissingBean({ReactiveMongoOperations.class})
    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate(ReactiveMongoDatabaseFactory reactiveMongoDatabaseFactory, MongoConverter converter) {
        return new ReactiveMongoTemplate(reactiveMongoDatabaseFactory, converter);
    }

    @ConditionalOnMissingBean({MongoConverter.class})
    @Bean
    public MappingMongoConverter mappingMongoConverter(MongoMappingContext context, MongoCustomConversions conversions) {
        MappingMongoConverter mappingConverter = new MappingMongoConverter(NoOpDbRefResolver.INSTANCE, context);
        mappingConverter.setCustomConversions(conversions);
        return mappingConverter;
    }

    @ConditionalOnMissingBean({DataBufferFactory.class})
    @Bean
    public DefaultDataBufferFactory dataBufferFactory() {
        return new DefaultDataBufferFactory();
    }

    @ConditionalOnMissingBean({ReactiveGridFsOperations.class})
    @Bean
    public ReactiveGridFsTemplate reactiveGridFsTemplate(ReactiveMongoDatabaseFactory reactiveMongoDatabaseFactory, MappingMongoConverter mappingMongoConverter, DataBufferFactory dataBufferFactory) {
        return new ReactiveGridFsTemplate(dataBufferFactory, new GridFsReactiveMongoDatabaseFactory(reactiveMongoDatabaseFactory, this.connectionDetails), mappingMongoConverter, this.connectionDetails.getGridFs() != null ? this.connectionDetails.getGridFs().getBucket() : null);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/mongo/MongoReactiveDataAutoConfiguration$GridFsReactiveMongoDatabaseFactory.class */
    static class GridFsReactiveMongoDatabaseFactory implements ReactiveMongoDatabaseFactory {
        private final ReactiveMongoDatabaseFactory delegate;
        private final MongoConnectionDetails connectionDetails;

        GridFsReactiveMongoDatabaseFactory(ReactiveMongoDatabaseFactory delegate, MongoConnectionDetails connectionDetails) {
            this.delegate = delegate;
            this.connectionDetails = connectionDetails;
        }

        public boolean hasCodecFor(Class<?> type) {
            return this.delegate.hasCodecFor(type);
        }

        public Mono<MongoDatabase> getMongoDatabase() throws DataAccessException {
            String gridFsDatabase = getGridFsDatabase(this.connectionDetails);
            if (StringUtils.hasText(gridFsDatabase)) {
                return this.delegate.getMongoDatabase(gridFsDatabase);
            }
            return this.delegate.getMongoDatabase();
        }

        private String getGridFsDatabase(MongoConnectionDetails connectionDetails) {
            if (connectionDetails.getGridFs() != null) {
                return connectionDetails.getGridFs().getDatabase();
            }
            return null;
        }

        public Mono<MongoDatabase> getMongoDatabase(String dbName) throws DataAccessException {
            return this.delegate.getMongoDatabase(dbName);
        }

        public <T> Optional<Codec<T>> getCodecFor(Class<T> type) {
            return this.delegate.getCodecFor(type);
        }

        public PersistenceExceptionTranslator getExceptionTranslator() {
            return this.delegate.getExceptionTranslator();
        }

        public CodecRegistry getCodecRegistry() {
            return this.delegate.getCodecRegistry();
        }

        public Mono<ClientSession> getSession(ClientSessionOptions options) {
            return this.delegate.getSession(options);
        }

        public ReactiveMongoDatabaseFactory withSession(ClientSession session) {
            return this.delegate.withSession(session);
        }

        public boolean isTransactionActive() {
            return this.delegate.isTransactionActive();
        }
    }
}
