package org.springframework.boot.autoconfigure.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.connection.SslSettings;
import org.bson.UuidRepresentation;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/mongo/StandardMongoClientSettingsBuilderCustomizer.class */
public class StandardMongoClientSettingsBuilderCustomizer implements MongoClientSettingsBuilderCustomizer, Ordered {
    private final ConnectionString connectionString;
    private final UuidRepresentation uuidRepresentation;
    private final MongoProperties.Ssl ssl;
    private final SslBundles sslBundles;
    private int order = 0;

    public StandardMongoClientSettingsBuilderCustomizer(ConnectionString connectionString, UuidRepresentation uuidRepresentation, MongoProperties.Ssl ssl, SslBundles sslBundles) {
        this.connectionString = connectionString;
        this.uuidRepresentation = uuidRepresentation;
        this.ssl = ssl;
        this.sslBundles = sslBundles;
    }

    @Override // org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer
    public void customize(MongoClientSettings.Builder settingsBuilder) {
        settingsBuilder.uuidRepresentation(this.uuidRepresentation);
        settingsBuilder.applyConnectionString(this.connectionString);
        if (this.ssl.isEnabled()) {
            settingsBuilder.applyToSslSettings(this::configureSsl);
        }
    }

    private void configureSsl(SslSettings.Builder settings) {
        settings.enabled(true);
        if (this.ssl.getBundle() != null) {
            SslBundle sslBundle = this.sslBundles.getBundle(this.ssl.getBundle());
            Assert.state(!sslBundle.getOptions().isSpecified(), "SSL options cannot be specified with MongoDB");
            settings.context(sslBundle.createSslContext());
        }
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
