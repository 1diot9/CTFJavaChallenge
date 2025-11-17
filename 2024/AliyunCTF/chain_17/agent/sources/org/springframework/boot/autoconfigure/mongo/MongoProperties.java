package org.springframework.boot.autoconfigure.mongo;

import com.mongodb.ConnectionString;
import java.util.List;
import org.bson.UuidRepresentation;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.data.mongodb")
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/mongo/MongoProperties.class */
public class MongoProperties {
    public static final int DEFAULT_PORT = 27017;
    public static final String DEFAULT_URI = "mongodb://localhost/test";
    private String host;
    private List<String> additionalHosts;
    private String uri;
    private String database;
    private String authenticationDatabase;
    private String username;
    private char[] password;
    private String replicaSetName;
    private Class<?> fieldNamingStrategy;
    private Boolean autoIndexCreation;
    private Integer port = null;
    private final Gridfs gridfs = new Gridfs();
    private UuidRepresentation uuidRepresentation = UuidRepresentation.JAVA_LEGACY;
    private final Ssl ssl = new Ssl();

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getDatabase() {
        return this.database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getAuthenticationDatabase() {
        return this.authenticationDatabase;
    }

    public void setAuthenticationDatabase(String authenticationDatabase) {
        this.authenticationDatabase = authenticationDatabase;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public char[] getPassword() {
        return this.password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public String getReplicaSetName() {
        return this.replicaSetName;
    }

    public void setReplicaSetName(String replicaSetName) {
        this.replicaSetName = replicaSetName;
    }

    public Class<?> getFieldNamingStrategy() {
        return this.fieldNamingStrategy;
    }

    public void setFieldNamingStrategy(Class<?> fieldNamingStrategy) {
        this.fieldNamingStrategy = fieldNamingStrategy;
    }

    public UuidRepresentation getUuidRepresentation() {
        return this.uuidRepresentation;
    }

    public void setUuidRepresentation(UuidRepresentation uuidRepresentation) {
        this.uuidRepresentation = uuidRepresentation;
    }

    public String getUri() {
        return this.uri;
    }

    public String determineUri() {
        return this.uri != null ? this.uri : DEFAULT_URI;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Integer getPort() {
        return this.port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Gridfs getGridfs() {
        return this.gridfs;
    }

    public String getMongoClientDatabase() {
        if (this.database != null) {
            return this.database;
        }
        return new ConnectionString(determineUri()).getDatabase();
    }

    public Boolean isAutoIndexCreation() {
        return this.autoIndexCreation;
    }

    public void setAutoIndexCreation(Boolean autoIndexCreation) {
        this.autoIndexCreation = autoIndexCreation;
    }

    public List<String> getAdditionalHosts() {
        return this.additionalHosts;
    }

    public void setAdditionalHosts(List<String> additionalHosts) {
        this.additionalHosts = additionalHosts;
    }

    public Ssl getSsl() {
        return this.ssl;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/mongo/MongoProperties$Gridfs.class */
    public static class Gridfs {
        private String database;
        private String bucket;

        public String getDatabase() {
            return this.database;
        }

        public void setDatabase(String database) {
            this.database = database;
        }

        public String getBucket() {
            return this.bucket;
        }

        public void setBucket(String bucket) {
            this.bucket = bucket;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/mongo/MongoProperties$Ssl.class */
    public static class Ssl {
        private Boolean enabled;
        private String bundle;

        public boolean isEnabled() {
            return this.enabled != null ? this.enabled.booleanValue() : this.bundle != null;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = Boolean.valueOf(enabled);
        }

        public String getBundle() {
            return this.bundle;
        }

        public void setBundle(String bundle) {
            this.bundle = bundle;
        }
    }
}
