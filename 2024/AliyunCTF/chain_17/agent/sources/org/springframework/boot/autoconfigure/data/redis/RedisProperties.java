package org.springframework.boot.autoconfigure.data.redis;

import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.data.redis")
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/redis/RedisProperties.class */
public class RedisProperties {
    private String url;
    private String username;
    private String password;
    private Duration timeout;
    private Duration connectTimeout;
    private String clientName;
    private ClientType clientType;
    private Sentinel sentinel;
    private Cluster cluster;
    private int database = 0;
    private String host = "localhost";
    private int port = 6379;
    private final Ssl ssl = new Ssl();
    private final Jedis jedis = new Jedis();
    private final Lettuce lettuce = new Lettuce();

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/redis/RedisProperties$ClientType.class */
    public enum ClientType {
        LETTUCE,
        JEDIS
    }

    public int getDatabase() {
        return this.database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
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

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Ssl getSsl() {
        return this.ssl;
    }

    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }

    public Duration getTimeout() {
        return this.timeout;
    }

    public Duration getConnectTimeout() {
        return this.connectTimeout;
    }

    public void setConnectTimeout(Duration connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public String getClientName() {
        return this.clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public ClientType getClientType() {
        return this.clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }

    public Sentinel getSentinel() {
        return this.sentinel;
    }

    public void setSentinel(Sentinel sentinel) {
        this.sentinel = sentinel;
    }

    public Cluster getCluster() {
        return this.cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public Jedis getJedis() {
        return this.jedis;
    }

    public Lettuce getLettuce() {
        return this.lettuce;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/redis/RedisProperties$Pool.class */
    public static class Pool {
        private Boolean enabled;
        private int maxIdle = 8;
        private int minIdle = 0;
        private int maxActive = 8;
        private Duration maxWait = Duration.ofMillis(-1);
        private Duration timeBetweenEvictionRuns;

        public Boolean getEnabled() {
            return this.enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public int getMaxIdle() {
            return this.maxIdle;
        }

        public void setMaxIdle(int maxIdle) {
            this.maxIdle = maxIdle;
        }

        public int getMinIdle() {
            return this.minIdle;
        }

        public void setMinIdle(int minIdle) {
            this.minIdle = minIdle;
        }

        public int getMaxActive() {
            return this.maxActive;
        }

        public void setMaxActive(int maxActive) {
            this.maxActive = maxActive;
        }

        public Duration getMaxWait() {
            return this.maxWait;
        }

        public void setMaxWait(Duration maxWait) {
            this.maxWait = maxWait;
        }

        public Duration getTimeBetweenEvictionRuns() {
            return this.timeBetweenEvictionRuns;
        }

        public void setTimeBetweenEvictionRuns(Duration timeBetweenEvictionRuns) {
            this.timeBetweenEvictionRuns = timeBetweenEvictionRuns;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/redis/RedisProperties$Cluster.class */
    public static class Cluster {
        private List<String> nodes;
        private Integer maxRedirects;

        public List<String> getNodes() {
            return this.nodes;
        }

        public void setNodes(List<String> nodes) {
            this.nodes = nodes;
        }

        public Integer getMaxRedirects() {
            return this.maxRedirects;
        }

        public void setMaxRedirects(Integer maxRedirects) {
            this.maxRedirects = maxRedirects;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/redis/RedisProperties$Sentinel.class */
    public static class Sentinel {
        private String master;
        private List<String> nodes;
        private String username;
        private String password;

        public String getMaster() {
            return this.master;
        }

        public void setMaster(String master) {
            this.master = master;
        }

        public List<String> getNodes() {
            return this.nodes;
        }

        public void setNodes(List<String> nodes) {
            this.nodes = nodes;
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
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/redis/RedisProperties$Ssl.class */
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

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/redis/RedisProperties$Jedis.class */
    public static class Jedis {
        private final Pool pool = new Pool();

        public Pool getPool() {
            return this.pool;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/redis/RedisProperties$Lettuce.class */
    public static class Lettuce {
        private Duration shutdownTimeout = Duration.ofMillis(100);
        private final Pool pool = new Pool();
        private final Cluster cluster = new Cluster();

        public Duration getShutdownTimeout() {
            return this.shutdownTimeout;
        }

        public void setShutdownTimeout(Duration shutdownTimeout) {
            this.shutdownTimeout = shutdownTimeout;
        }

        public Pool getPool() {
            return this.pool;
        }

        public Cluster getCluster() {
            return this.cluster;
        }

        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/redis/RedisProperties$Lettuce$Cluster.class */
        public static class Cluster {
            private final Refresh refresh = new Refresh();

            public Refresh getRefresh() {
                return this.refresh;
            }

            /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/redis/RedisProperties$Lettuce$Cluster$Refresh.class */
            public static class Refresh {
                private boolean dynamicRefreshSources = true;
                private Duration period;
                private boolean adaptive;

                public boolean isDynamicRefreshSources() {
                    return this.dynamicRefreshSources;
                }

                public void setDynamicRefreshSources(boolean dynamicRefreshSources) {
                    this.dynamicRefreshSources = dynamicRefreshSources;
                }

                public Duration getPeriod() {
                    return this.period;
                }

                public void setPeriod(Duration period) {
                    this.period = period;
                }

                public boolean isAdaptive() {
                    return this.adaptive;
                }

                public void setAdaptive(boolean adaptive) {
                    this.adaptive = adaptive;
                }
            }
        }
    }
}
