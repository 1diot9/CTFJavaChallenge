package org.springframework.boot.autoconfigure.mongo;

import com.mongodb.MongoClientSettings;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/mongo/MongoClientSettingsBuilderCustomizer.class */
public interface MongoClientSettingsBuilderCustomizer {
    void customize(MongoClientSettings.Builder clientSettingsBuilder);
}
