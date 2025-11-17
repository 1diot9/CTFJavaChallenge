package org.springframework.boot.autoconfigure.couchbase;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "spring.couchbase")
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/couchbase/CouchbaseProperties.class */
public class CouchbaseProperties {
    private String connectionString;
    private String username;
    private String password;
    private final Env env = new Env();

    public String getConnectionString() {
        return this.connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
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

    public Env getEnv() {
        return this.env;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/couchbase/CouchbaseProperties$Env.class */
    public static class Env {

        /* renamed from: io, reason: collision with root package name */
        private final Io f5io = new Io();
        private final Ssl ssl = new Ssl();
        private final Timeouts timeouts = new Timeouts();

        public Io getIo() {
            return this.f5io;
        }

        public Ssl getSsl() {
            return this.ssl;
        }

        public Timeouts getTimeouts() {
            return this.timeouts;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/couchbase/CouchbaseProperties$Io.class */
    public static class Io {
        private int minEndpoints = 1;
        private int maxEndpoints = 12;
        private Duration idleHttpConnectionTimeout = Duration.ofSeconds(1);

        public int getMinEndpoints() {
            return this.minEndpoints;
        }

        public void setMinEndpoints(int minEndpoints) {
            this.minEndpoints = minEndpoints;
        }

        public int getMaxEndpoints() {
            return this.maxEndpoints;
        }

        public void setMaxEndpoints(int maxEndpoints) {
            this.maxEndpoints = maxEndpoints;
        }

        public Duration getIdleHttpConnectionTimeout() {
            return this.idleHttpConnectionTimeout;
        }

        public void setIdleHttpConnectionTimeout(Duration idleHttpConnectionTimeout) {
            this.idleHttpConnectionTimeout = idleHttpConnectionTimeout;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/couchbase/CouchbaseProperties$Ssl.class */
    public static class Ssl {
        private Boolean enabled;
        private String keyStore;
        private String keyStorePassword;
        private String bundle;

        public Boolean getEnabled() {
            boolean z;
            if (this.enabled != null) {
                z = this.enabled.booleanValue();
            } else {
                z = StringUtils.hasText(this.keyStore) || StringUtils.hasText(this.bundle);
            }
            return Boolean.valueOf(z);
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        @DeprecatedConfigurationProperty(reason = "SSL bundle support with spring.ssl.bundle and spring.couchbase.env.ssl.bundle should be used instead", since = "3.1.0")
        @Deprecated(since = "3.1.0", forRemoval = true)
        public String getKeyStore() {
            return this.keyStore;
        }

        @Deprecated(since = "3.1.0", forRemoval = true)
        public void setKeyStore(String keyStore) {
            this.keyStore = keyStore;
        }

        @DeprecatedConfigurationProperty(reason = "SSL bundle support with spring.ssl.bundle and spring.couchbase.env.ssl.bundle should be used instead", since = "3.1.0")
        @Deprecated(since = "3.1.0", forRemoval = true)
        public String getKeyStorePassword() {
            return this.keyStorePassword;
        }

        @Deprecated(since = "3.1.0", forRemoval = true)
        public void setKeyStorePassword(String keyStorePassword) {
            this.keyStorePassword = keyStorePassword;
        }

        public String getBundle() {
            return this.bundle;
        }

        public void setBundle(String bundle) {
            this.bundle = bundle;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/couchbase/CouchbaseProperties$Timeouts.class */
    public static class Timeouts {
        private Duration connect = Duration.ofSeconds(10);
        private Duration disconnect = Duration.ofSeconds(10);
        private Duration keyValue = Duration.ofMillis(2500);
        private Duration keyValueDurable = Duration.ofSeconds(10);
        private Duration query = Duration.ofSeconds(75);
        private Duration view = Duration.ofSeconds(75);
        private Duration search = Duration.ofSeconds(75);
        private Duration analytics = Duration.ofSeconds(75);
        private Duration management = Duration.ofSeconds(75);

        public Duration getConnect() {
            return this.connect;
        }

        public void setConnect(Duration connect) {
            this.connect = connect;
        }

        public Duration getDisconnect() {
            return this.disconnect;
        }

        public void setDisconnect(Duration disconnect) {
            this.disconnect = disconnect;
        }

        public Duration getKeyValue() {
            return this.keyValue;
        }

        public void setKeyValue(Duration keyValue) {
            this.keyValue = keyValue;
        }

        public Duration getKeyValueDurable() {
            return this.keyValueDurable;
        }

        public void setKeyValueDurable(Duration keyValueDurable) {
            this.keyValueDurable = keyValueDurable;
        }

        public Duration getQuery() {
            return this.query;
        }

        public void setQuery(Duration query) {
            this.query = query;
        }

        public Duration getView() {
            return this.view;
        }

        public void setView(Duration view) {
            this.view = view;
        }

        public Duration getSearch() {
            return this.search;
        }

        public void setSearch(Duration search) {
            this.search = search;
        }

        public Duration getAnalytics() {
            return this.analytics;
        }

        public void setAnalytics(Duration analytics) {
            this.analytics = analytics;
        }

        public Duration getManagement() {
            return this.management;
        }

        public void setManagement(Duration management) {
            this.management = management;
        }
    }
}
