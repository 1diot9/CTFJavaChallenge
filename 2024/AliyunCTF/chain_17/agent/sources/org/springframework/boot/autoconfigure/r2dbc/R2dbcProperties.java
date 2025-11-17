package org.springframework.boot.autoconfigure.r2dbc;

import io.r2dbc.spi.ValidationDepth;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.r2dbc")
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/r2dbc/R2dbcProperties.class */
public class R2dbcProperties {
    private String name;
    private boolean generateUniqueName;
    private String url;
    private String username;
    private String password;
    private final Map<String, String> properties = new LinkedHashMap();
    private final Pool pool = new Pool();
    private String uniqueName;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isGenerateUniqueName() {
        return this.generateUniqueName;
    }

    public void setGenerateUniqueName(boolean generateUniqueName) {
        this.generateUniqueName = generateUniqueName;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public Pool getPool() {
        return this.pool;
    }

    public String determineUniqueName() {
        if (this.uniqueName == null) {
            this.uniqueName = UUID.randomUUID().toString();
        }
        return this.uniqueName;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/r2dbc/R2dbcProperties$Pool.class */
    public static class Pool {
        private Duration maxLifeTime;
        private Duration maxAcquireTime;
        private Duration maxValidationTime;
        private Duration maxCreateConnectionTime;
        private String validationQuery;
        private int minIdle = 0;
        private Duration maxIdleTime = Duration.ofMinutes(30);
        private int initialSize = 10;
        private int maxSize = 10;
        private ValidationDepth validationDepth = ValidationDepth.LOCAL;
        private boolean enabled = true;

        public int getMinIdle() {
            return this.minIdle;
        }

        public void setMinIdle(int minIdle) {
            this.minIdle = minIdle;
        }

        public Duration getMaxIdleTime() {
            return this.maxIdleTime;
        }

        public void setMaxIdleTime(Duration maxIdleTime) {
            this.maxIdleTime = maxIdleTime;
        }

        public Duration getMaxLifeTime() {
            return this.maxLifeTime;
        }

        public void setMaxLifeTime(Duration maxLifeTime) {
            this.maxLifeTime = maxLifeTime;
        }

        public Duration getMaxValidationTime() {
            return this.maxValidationTime;
        }

        public void setMaxValidationTime(Duration maxValidationTime) {
            this.maxValidationTime = maxValidationTime;
        }

        public Duration getMaxAcquireTime() {
            return this.maxAcquireTime;
        }

        public void setMaxAcquireTime(Duration maxAcquireTime) {
            this.maxAcquireTime = maxAcquireTime;
        }

        public Duration getMaxCreateConnectionTime() {
            return this.maxCreateConnectionTime;
        }

        public void setMaxCreateConnectionTime(Duration maxCreateConnectionTime) {
            this.maxCreateConnectionTime = maxCreateConnectionTime;
        }

        public int getInitialSize() {
            return this.initialSize;
        }

        public void setInitialSize(int initialSize) {
            this.initialSize = initialSize;
        }

        public int getMaxSize() {
            return this.maxSize;
        }

        public void setMaxSize(int maxSize) {
            this.maxSize = maxSize;
        }

        public String getValidationQuery() {
            return this.validationQuery;
        }

        public void setValidationQuery(String validationQuery) {
            this.validationQuery = validationQuery;
        }

        public ValidationDepth getValidationDepth() {
            return this.validationDepth;
        }

        public void setValidationDepth(ValidationDepth validationDepth) {
            this.validationDepth = validationDepth;
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
