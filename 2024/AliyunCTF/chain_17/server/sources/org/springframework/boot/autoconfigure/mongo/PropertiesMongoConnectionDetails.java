package org.springframework.boot.autoconfigure.mongo;

import ch.qos.logback.core.CoreConstants;
import com.mongodb.ConnectionString;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.mongo.MongoConnectionDetails;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/mongo/PropertiesMongoConnectionDetails.class */
public class PropertiesMongoConnectionDetails implements MongoConnectionDetails {
    private final MongoProperties properties;

    public PropertiesMongoConnectionDetails(MongoProperties properties) {
        this.properties = properties;
    }

    @Override // org.springframework.boot.autoconfigure.mongo.MongoConnectionDetails
    public ConnectionString getConnectionString() {
        if (this.properties.getUri() != null) {
            return new ConnectionString(this.properties.getUri());
        }
        StringBuilder builder = new StringBuilder("mongodb://");
        if (this.properties.getUsername() != null) {
            builder.append(encode(this.properties.getUsername()));
            builder.append(":");
            if (this.properties.getPassword() != null) {
                builder.append(encode(this.properties.getPassword()));
            }
            builder.append("@");
        }
        builder.append(this.properties.getHost() != null ? this.properties.getHost() : "localhost");
        if (this.properties.getPort() != null) {
            builder.append(":");
            builder.append(this.properties.getPort());
        }
        if (this.properties.getAdditionalHosts() != null) {
            builder.append(",");
            builder.append(String.join(",", this.properties.getAdditionalHosts()));
        }
        builder.append("/");
        builder.append(this.properties.getMongoClientDatabase());
        List<String> options = getOptions();
        if (!options.isEmpty()) {
            builder.append(CoreConstants.NA);
            builder.append(String.join(BeanFactory.FACTORY_BEAN_PREFIX, options));
        }
        return new ConnectionString(builder.toString());
    }

    private String encode(String input) {
        return URLEncoder.encode(input, StandardCharsets.UTF_8);
    }

    private char[] encode(char[] input) {
        return URLEncoder.encode(new String(input), StandardCharsets.UTF_8).toCharArray();
    }

    @Override // org.springframework.boot.autoconfigure.mongo.MongoConnectionDetails
    public MongoConnectionDetails.GridFs getGridFs() {
        return MongoConnectionDetails.GridFs.of(this.properties.getGridfs().getDatabase(), this.properties.getGridfs().getBucket());
    }

    private List<String> getOptions() {
        List<String> options = new ArrayList<>();
        if (this.properties.getReplicaSetName() != null) {
            options.add("replicaSet=" + this.properties.getReplicaSetName());
        }
        if (this.properties.getUsername() != null && this.properties.getAuthenticationDatabase() != null) {
            options.add("authSource=" + this.properties.getAuthenticationDatabase());
        }
        return options;
    }
}
