package org.springframework.boot.autoconfigure.neo4j;

import java.io.File;
import java.net.URI;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.neo4j")
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/neo4j/Neo4jProperties.class */
public class Neo4jProperties {
    private URI uri;
    private Duration connectionTimeout = Duration.ofSeconds(30);
    private Duration maxTransactionRetryTime = Duration.ofSeconds(30);
    private final Authentication authentication = new Authentication();
    private final Pool pool = new Pool();
    private final Security security = new Security();

    public URI getUri() {
        return this.uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public Duration getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public void setConnectionTimeout(Duration connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Duration getMaxTransactionRetryTime() {
        return this.maxTransactionRetryTime;
    }

    public void setMaxTransactionRetryTime(Duration maxTransactionRetryTime) {
        this.maxTransactionRetryTime = maxTransactionRetryTime;
    }

    public Authentication getAuthentication() {
        return this.authentication;
    }

    public Pool getPool() {
        return this.pool;
    }

    public Security getSecurity() {
        return this.security;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/neo4j/Neo4jProperties$Authentication.class */
    public static class Authentication {
        private String username;
        private String password;
        private String realm;
        private String kerberosTicket;

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

        public String getRealm() {
            return this.realm;
        }

        public void setRealm(String realm) {
            this.realm = realm;
        }

        public String getKerberosTicket() {
            return this.kerberosTicket;
        }

        public void setKerberosTicket(String kerberosTicket) {
            this.kerberosTicket = kerberosTicket;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/neo4j/Neo4jProperties$Pool.class */
    public static class Pool {
        private Duration idleTimeBeforeConnectionTest;
        private boolean metricsEnabled = false;
        private boolean logLeakedSessions = false;
        private int maxConnectionPoolSize = 100;
        private Duration maxConnectionLifetime = Duration.ofHours(1);
        private Duration connectionAcquisitionTimeout = Duration.ofSeconds(60);

        public boolean isLogLeakedSessions() {
            return this.logLeakedSessions;
        }

        public void setLogLeakedSessions(boolean logLeakedSessions) {
            this.logLeakedSessions = logLeakedSessions;
        }

        public int getMaxConnectionPoolSize() {
            return this.maxConnectionPoolSize;
        }

        public void setMaxConnectionPoolSize(int maxConnectionPoolSize) {
            this.maxConnectionPoolSize = maxConnectionPoolSize;
        }

        public Duration getIdleTimeBeforeConnectionTest() {
            return this.idleTimeBeforeConnectionTest;
        }

        public void setIdleTimeBeforeConnectionTest(Duration idleTimeBeforeConnectionTest) {
            this.idleTimeBeforeConnectionTest = idleTimeBeforeConnectionTest;
        }

        public Duration getMaxConnectionLifetime() {
            return this.maxConnectionLifetime;
        }

        public void setMaxConnectionLifetime(Duration maxConnectionLifetime) {
            this.maxConnectionLifetime = maxConnectionLifetime;
        }

        public Duration getConnectionAcquisitionTimeout() {
            return this.connectionAcquisitionTimeout;
        }

        public void setConnectionAcquisitionTimeout(Duration connectionAcquisitionTimeout) {
            this.connectionAcquisitionTimeout = connectionAcquisitionTimeout;
        }

        public boolean isMetricsEnabled() {
            return this.metricsEnabled;
        }

        public void setMetricsEnabled(boolean metricsEnabled) {
            this.metricsEnabled = metricsEnabled;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/neo4j/Neo4jProperties$Security.class */
    public static class Security {
        private File certFile;
        private boolean encrypted = false;
        private TrustStrategy trustStrategy = TrustStrategy.TRUST_SYSTEM_CA_SIGNED_CERTIFICATES;
        private boolean hostnameVerificationEnabled = true;

        /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/neo4j/Neo4jProperties$Security$TrustStrategy.class */
        public enum TrustStrategy {
            TRUST_ALL_CERTIFICATES,
            TRUST_CUSTOM_CA_SIGNED_CERTIFICATES,
            TRUST_SYSTEM_CA_SIGNED_CERTIFICATES
        }

        public boolean isEncrypted() {
            return this.encrypted;
        }

        public void setEncrypted(boolean encrypted) {
            this.encrypted = encrypted;
        }

        public TrustStrategy getTrustStrategy() {
            return this.trustStrategy;
        }

        public void setTrustStrategy(TrustStrategy trustStrategy) {
            this.trustStrategy = trustStrategy;
        }

        public File getCertFile() {
            return this.certFile;
        }

        public void setCertFile(File certFile) {
            this.certFile = certFile;
        }

        public boolean isHostnameVerificationEnabled() {
            return this.hostnameVerificationEnabled;
        }

        public void setHostnameVerificationEnabled(boolean hostnameVerificationEnabled) {
            this.hostnameVerificationEnabled = hostnameVerificationEnabled;
        }
    }
}
