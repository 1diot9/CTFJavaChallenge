package org.springframework.boot.autoconfigure.cassandra;

import com.datastax.oss.driver.api.core.DefaultConsistencyLevel;
import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

@ConfigurationProperties(prefix = "spring.cassandra")
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/cassandra/CassandraProperties.class */
public class CassandraProperties {
    private Resource config;
    private String keyspaceName;
    private String sessionName;
    private List<String> contactPoints;
    private String localDatacenter;
    private String username;
    private String password;
    private Compression compression;
    private int port = 9042;
    private String schemaAction = "none";
    private Ssl ssl = new Ssl();
    private final Connection connection = new Connection();
    private final Pool pool = new Pool();
    private final Request request = new Request();
    private final Controlconnection controlconnection = new Controlconnection();

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/cassandra/CassandraProperties$Compression.class */
    public enum Compression {
        LZ4,
        SNAPPY,
        NONE
    }

    public Resource getConfig() {
        return this.config;
    }

    public void setConfig(Resource config) {
        this.config = config;
    }

    public String getKeyspaceName() {
        return this.keyspaceName;
    }

    public void setKeyspaceName(String keyspaceName) {
        this.keyspaceName = keyspaceName;
    }

    public String getSessionName() {
        return this.sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public List<String> getContactPoints() {
        return this.contactPoints;
    }

    public void setContactPoints(List<String> contactPoints) {
        this.contactPoints = contactPoints;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getLocalDatacenter() {
        return this.localDatacenter;
    }

    public void setLocalDatacenter(String localDatacenter) {
        this.localDatacenter = localDatacenter;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Compression getCompression() {
        return this.compression;
    }

    public void setCompression(Compression compression) {
        this.compression = compression;
    }

    public Ssl getSsl() {
        return this.ssl;
    }

    public void setSsl(Ssl ssl) {
        this.ssl = ssl;
    }

    public String getSchemaAction() {
        return this.schemaAction;
    }

    public void setSchemaAction(String schemaAction) {
        this.schemaAction = schemaAction;
    }

    public Connection getConnection() {
        return this.connection;
    }

    public Pool getPool() {
        return this.pool;
    }

    public Request getRequest() {
        return this.request;
    }

    public Controlconnection getControlconnection() {
        return this.controlconnection;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/cassandra/CassandraProperties$Ssl.class */
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

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/cassandra/CassandraProperties$Connection.class */
    public static class Connection {
        private Duration connectTimeout;
        private Duration initQueryTimeout;

        public Duration getConnectTimeout() {
            return this.connectTimeout;
        }

        public void setConnectTimeout(Duration connectTimeout) {
            this.connectTimeout = connectTimeout;
        }

        public Duration getInitQueryTimeout() {
            return this.initQueryTimeout;
        }

        public void setInitQueryTimeout(Duration initQueryTimeout) {
            this.initQueryTimeout = initQueryTimeout;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/cassandra/CassandraProperties$Request.class */
    public static class Request {
        private Duration timeout;
        private DefaultConsistencyLevel consistency;
        private DefaultConsistencyLevel serialConsistency;
        private Integer pageSize;
        private final Throttler throttler = new Throttler();

        public Duration getTimeout() {
            return this.timeout;
        }

        public void setTimeout(Duration timeout) {
            this.timeout = timeout;
        }

        public DefaultConsistencyLevel getConsistency() {
            return this.consistency;
        }

        public void setConsistency(DefaultConsistencyLevel consistency) {
            this.consistency = consistency;
        }

        public DefaultConsistencyLevel getSerialConsistency() {
            return this.serialConsistency;
        }

        public void setSerialConsistency(DefaultConsistencyLevel serialConsistency) {
            this.serialConsistency = serialConsistency;
        }

        public Integer getPageSize() {
            return this.pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = Integer.valueOf(pageSize);
        }

        public Throttler getThrottler() {
            return this.throttler;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/cassandra/CassandraProperties$Pool.class */
    public static class Pool {
        private Duration idleTimeout;
        private Duration heartbeatInterval;

        public Duration getIdleTimeout() {
            return this.idleTimeout;
        }

        public void setIdleTimeout(Duration idleTimeout) {
            this.idleTimeout = idleTimeout;
        }

        public Duration getHeartbeatInterval() {
            return this.heartbeatInterval;
        }

        public void setHeartbeatInterval(Duration heartbeatInterval) {
            this.heartbeatInterval = heartbeatInterval;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/cassandra/CassandraProperties$Controlconnection.class */
    public static class Controlconnection {
        private Duration timeout;

        public Duration getTimeout() {
            return this.timeout;
        }

        public void setTimeout(Duration timeout) {
            this.timeout = timeout;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/cassandra/CassandraProperties$Throttler.class */
    public static class Throttler {
        private ThrottlerType type;
        private Integer maxQueueSize;
        private Integer maxConcurrentRequests;
        private Integer maxRequestsPerSecond;
        private Duration drainInterval;

        public ThrottlerType getType() {
            return this.type;
        }

        public void setType(ThrottlerType type) {
            this.type = type;
        }

        public Integer getMaxQueueSize() {
            return this.maxQueueSize;
        }

        public void setMaxQueueSize(int maxQueueSize) {
            this.maxQueueSize = Integer.valueOf(maxQueueSize);
        }

        public Integer getMaxConcurrentRequests() {
            return this.maxConcurrentRequests;
        }

        public void setMaxConcurrentRequests(int maxConcurrentRequests) {
            this.maxConcurrentRequests = Integer.valueOf(maxConcurrentRequests);
        }

        public Integer getMaxRequestsPerSecond() {
            return this.maxRequestsPerSecond;
        }

        public void setMaxRequestsPerSecond(int maxRequestsPerSecond) {
            this.maxRequestsPerSecond = Integer.valueOf(maxRequestsPerSecond);
        }

        public Duration getDrainInterval() {
            return this.drainInterval;
        }

        public void setDrainInterval(Duration drainInterval) {
            this.drainInterval = drainInterval;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/cassandra/CassandraProperties$ThrottlerType.class */
    public enum ThrottlerType {
        CONCURRENCY_LIMITING("ConcurrencyLimitingRequestThrottler"),
        RATE_LIMITING("RateLimitingRequestThrottler"),
        NONE("PassThroughRequestThrottler");

        private final String type;

        ThrottlerType(String type) {
            this.type = type;
        }

        public String type() {
            return this.type;
        }
    }
}
