package org.springframework.boot.autoconfigure.cache;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

@ConfigurationProperties(prefix = "spring.cache")
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/cache/CacheProperties.class */
public class CacheProperties {
    private CacheType type;
    private List<String> cacheNames = new ArrayList();
    private final Caffeine caffeine = new Caffeine();
    private final Couchbase couchbase = new Couchbase();
    private final Infinispan infinispan = new Infinispan();
    private final JCache jcache = new JCache();
    private final Redis redis = new Redis();

    public CacheType getType() {
        return this.type;
    }

    public void setType(CacheType mode) {
        this.type = mode;
    }

    public List<String> getCacheNames() {
        return this.cacheNames;
    }

    public void setCacheNames(List<String> cacheNames) {
        this.cacheNames = cacheNames;
    }

    public Caffeine getCaffeine() {
        return this.caffeine;
    }

    public Couchbase getCouchbase() {
        return this.couchbase;
    }

    public Infinispan getInfinispan() {
        return this.infinispan;
    }

    public JCache getJcache() {
        return this.jcache;
    }

    public Redis getRedis() {
        return this.redis;
    }

    public Resource resolveConfigLocation(Resource config) {
        if (config != null) {
            Assert.isTrue(config.exists(), (Supplier<String>) () -> {
                return "Cache configuration does not exist '" + config.getDescription() + "'";
            });
            return config;
        }
        return null;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/cache/CacheProperties$Caffeine.class */
    public static class Caffeine {
        private String spec;

        public String getSpec() {
            return this.spec;
        }

        public void setSpec(String spec) {
            this.spec = spec;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/cache/CacheProperties$Couchbase.class */
    public static class Couchbase {
        private Duration expiration;

        public Duration getExpiration() {
            return this.expiration;
        }

        public void setExpiration(Duration expiration) {
            this.expiration = expiration;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/cache/CacheProperties$Infinispan.class */
    public static class Infinispan {
        private Resource config;

        public Resource getConfig() {
            return this.config;
        }

        public void setConfig(Resource config) {
            this.config = config;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/cache/CacheProperties$JCache.class */
    public static class JCache {
        private Resource config;
        private String provider;

        public String getProvider() {
            return this.provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public Resource getConfig() {
            return this.config;
        }

        public void setConfig(Resource config) {
            this.config = config;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/cache/CacheProperties$Redis.class */
    public static class Redis {
        private Duration timeToLive;
        private String keyPrefix;
        private boolean enableStatistics;
        private boolean cacheNullValues = true;
        private boolean useKeyPrefix = true;

        public Duration getTimeToLive() {
            return this.timeToLive;
        }

        public void setTimeToLive(Duration timeToLive) {
            this.timeToLive = timeToLive;
        }

        public boolean isCacheNullValues() {
            return this.cacheNullValues;
        }

        public void setCacheNullValues(boolean cacheNullValues) {
            this.cacheNullValues = cacheNullValues;
        }

        public String getKeyPrefix() {
            return this.keyPrefix;
        }

        public void setKeyPrefix(String keyPrefix) {
            this.keyPrefix = keyPrefix;
        }

        public boolean isUseKeyPrefix() {
            return this.useKeyPrefix;
        }

        public void setUseKeyPrefix(boolean useKeyPrefix) {
            this.useKeyPrefix = useKeyPrefix;
        }

        public boolean isEnableStatistics() {
            return this.enableStatistics;
        }

        public void setEnableStatistics(boolean enableStatistics) {
            this.enableStatistics = enableStatistics;
        }
    }
}
