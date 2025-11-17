package org.springframework.boot.logging;

import java.util.Objects;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/LoggerConfiguration.class */
public final class LoggerConfiguration {
    private final String name;
    private final LevelConfiguration levelConfiguration;
    private final LevelConfiguration inheritedLevelConfiguration;

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/LoggerConfiguration$ConfigurationScope.class */
    public enum ConfigurationScope {
        DIRECT,
        INHERITED
    }

    public LoggerConfiguration(String name, LogLevel configuredLevel, LogLevel effectiveLevel) {
        Assert.notNull(name, "Name must not be null");
        Assert.notNull(effectiveLevel, "EffectiveLevel must not be null");
        this.name = name;
        this.levelConfiguration = configuredLevel != null ? LevelConfiguration.of(configuredLevel) : null;
        this.inheritedLevelConfiguration = LevelConfiguration.of(effectiveLevel);
    }

    public LoggerConfiguration(String name, LevelConfiguration levelConfiguration, LevelConfiguration inheritedLevelConfiguration) {
        Assert.notNull(name, "Name must not be null");
        Assert.notNull(inheritedLevelConfiguration, "InheritedLevelConfiguration must not be null");
        this.name = name;
        this.levelConfiguration = levelConfiguration;
        this.inheritedLevelConfiguration = inheritedLevelConfiguration;
    }

    public String getName() {
        return this.name;
    }

    public LogLevel getConfiguredLevel() {
        LevelConfiguration configuration = getLevelConfiguration(ConfigurationScope.DIRECT);
        if (configuration != null) {
            return configuration.getLevel();
        }
        return null;
    }

    public LogLevel getEffectiveLevel() {
        return getLevelConfiguration().getLevel();
    }

    public LevelConfiguration getLevelConfiguration() {
        return getLevelConfiguration(ConfigurationScope.INHERITED);
    }

    public LevelConfiguration getLevelConfiguration(ConfigurationScope scope) {
        return scope != ConfigurationScope.DIRECT ? this.inheritedLevelConfiguration : this.levelConfiguration;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        LoggerConfiguration other = (LoggerConfiguration) obj;
        return ObjectUtils.nullSafeEquals(this.name, other.name) && ObjectUtils.nullSafeEquals(this.levelConfiguration, other.levelConfiguration) && ObjectUtils.nullSafeEquals(this.inheritedLevelConfiguration, other.inheritedLevelConfiguration);
    }

    public int hashCode() {
        return Objects.hash(this.name, this.levelConfiguration, this.inheritedLevelConfiguration);
    }

    public String toString() {
        return "LoggerConfiguration [name=" + this.name + ", levelConfiguration=" + this.levelConfiguration + ", inheritedLevelConfiguration=" + this.inheritedLevelConfiguration + "]";
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/LoggerConfiguration$LevelConfiguration.class */
    public static final class LevelConfiguration {
        private final String name;
        private final LogLevel logLevel;

        private LevelConfiguration(String name, LogLevel logLevel) {
            this.name = name;
            this.logLevel = logLevel;
        }

        public String getName() {
            return this.name;
        }

        public LogLevel getLevel() {
            Assert.state(this.logLevel != null, "Unable to provide LogLevel for '" + this.name + "'");
            return this.logLevel;
        }

        public boolean isCustom() {
            return this.logLevel == null;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            LevelConfiguration other = (LevelConfiguration) obj;
            return this.logLevel == other.logLevel && ObjectUtils.nullSafeEquals(this.name, other.name);
        }

        public int hashCode() {
            return Objects.hash(this.logLevel, this.name);
        }

        public String toString() {
            return "LevelConfiguration [name=" + this.name + ", logLevel=" + this.logLevel + "]";
        }

        public static LevelConfiguration of(LogLevel logLevel) {
            Assert.notNull(logLevel, "LogLevel must not be null");
            return new LevelConfiguration(logLevel.name(), logLevel);
        }

        public static LevelConfiguration ofCustom(String name) {
            Assert.hasText(name, "Name must not be empty");
            return new LevelConfiguration(name, null);
        }
    }
}
