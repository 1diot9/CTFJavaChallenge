package org.springframework.boot.autoconfigure.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.springframework.core.Ordered;
import org.springframework.util.CollectionUtils;

@Deprecated(since = "3.1.0", forRemoval = true)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/mongo/MongoPropertiesClientSettingsBuilderCustomizer.class */
public class MongoPropertiesClientSettingsBuilderCustomizer implements MongoClientSettingsBuilderCustomizer, Ordered {
    private final MongoProperties properties;
    private int order = 0;

    public MongoPropertiesClientSettingsBuilderCustomizer(MongoProperties properties) {
        this.properties = properties;
    }

    @Override // org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer
    public void customize(MongoClientSettings.Builder settingsBuilder) {
        applyUuidRepresentation(settingsBuilder);
        applyHostAndPort(settingsBuilder);
        applyCredentials(settingsBuilder);
        applyReplicaSet(settingsBuilder);
    }

    private void applyUuidRepresentation(MongoClientSettings.Builder settingsBuilder) {
        settingsBuilder.uuidRepresentation(this.properties.getUuidRepresentation());
    }

    private void applyHostAndPort(MongoClientSettings.Builder settings) {
        if (this.properties.getUri() != null) {
            settings.applyConnectionString(new ConnectionString(this.properties.getUri()));
            return;
        }
        if (this.properties.getHost() != null || this.properties.getPort() != null) {
            String host = (String) getOrDefault(this.properties.getHost(), "localhost");
            int port = ((Integer) getOrDefault(this.properties.getPort(), Integer.valueOf(MongoProperties.DEFAULT_PORT))).intValue();
            List<ServerAddress> serverAddresses = new ArrayList<>();
            serverAddresses.add(new ServerAddress(host, port));
            if (!CollectionUtils.isEmpty(this.properties.getAdditionalHosts())) {
                Stream<R> map = this.properties.getAdditionalHosts().stream().map(ServerAddress::new);
                Objects.requireNonNull(serverAddresses);
                map.forEach((v1) -> {
                    r1.add(v1);
                });
            }
            settings.applyToClusterSettings(cluster -> {
                cluster.hosts(serverAddresses);
            });
            return;
        }
        settings.applyConnectionString(new ConnectionString(MongoProperties.DEFAULT_URI));
    }

    private void applyCredentials(MongoClientSettings.Builder builder) {
        if (this.properties.getUri() == null && this.properties.getUsername() != null && this.properties.getPassword() != null) {
            String database = this.properties.getAuthenticationDatabase() != null ? this.properties.getAuthenticationDatabase() : this.properties.getMongoClientDatabase();
            builder.credential(MongoCredential.createCredential(this.properties.getUsername(), database, this.properties.getPassword()));
        }
    }

    private void applyReplicaSet(MongoClientSettings.Builder builder) {
        if (this.properties.getReplicaSetName() != null) {
            builder.applyToClusterSettings(cluster -> {
                cluster.requiredReplicaSetName(this.properties.getReplicaSetName());
            });
        }
    }

    private <V> V getOrDefault(V value, V defaultValue) {
        return value != null ? value : defaultValue;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
