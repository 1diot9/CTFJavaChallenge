package org.springframework.boot.autoconfigure.mongo;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import java.util.List;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/mongo/ReactiveMongoClientFactory.class */
public class ReactiveMongoClientFactory extends MongoClientFactorySupport<MongoClient> {
    public ReactiveMongoClientFactory(List<MongoClientSettingsBuilderCustomizer> builderCustomizers) {
        super(builderCustomizers, MongoClients::create);
    }
}
