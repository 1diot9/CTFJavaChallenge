package org.springframework.boot.autoconfigure.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/mongo/MongoClientFactory.class */
public class MongoClientFactory extends MongoClientFactorySupport<MongoClient> {
    public MongoClientFactory(List<MongoClientSettingsBuilderCustomizer> builderCustomizers) {
        super(builderCustomizers, MongoClients::create);
    }
}
