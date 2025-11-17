package org.springframework.boot.logging.log4j2;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.arbiters.Arbiter;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginLoggerContext;
import org.apache.logging.log4j.status.StatusLogger;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.util.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
@Plugin(name = "SpringProfile", category = "Core", elementType = "Arbiter", deferChildren = true, printObject = true)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/log4j2/SpringProfileArbiter.class */
public final class SpringProfileArbiter implements Arbiter {
    private final Environment environment;
    private final Profiles profiles;

    private SpringProfileArbiter(Environment environment, String[] profiles) {
        this.environment = environment;
        this.profiles = Profiles.of(profiles);
    }

    public boolean isCondition() {
        if (this.environment != null) {
            return this.environment.acceptsProfiles(this.profiles);
        }
        return false;
    }

    @PluginBuilderFactory
    static Builder newBuilder() {
        return new Builder();
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/log4j2/SpringProfileArbiter$Builder.class */
    static final class Builder implements org.apache.logging.log4j.core.util.Builder<SpringProfileArbiter> {
        private static final Logger statusLogger = StatusLogger.getLogger();

        @PluginBuilderAttribute
        private String name;

        @PluginConfiguration
        private Configuration configuration;

        @PluginLoggerContext
        private LoggerContext loggerContext;

        private Builder() {
        }

        Builder setName(String name) {
            this.name = name;
            return this;
        }

        /* renamed from: build, reason: merged with bridge method [inline-methods] */
        public SpringProfileArbiter m2172build() {
            Environment environment = Log4J2LoggingSystem.getEnvironment(this.loggerContext);
            if (environment == null) {
                statusLogger.warn("Cannot create Arbiter, no Spring Environment available");
                return null;
            }
            String name = this.configuration.getStrSubstitutor().replace(this.name);
            String[] profiles = StringUtils.trimArrayElements(StringUtils.commaDelimitedListToStringArray(name));
            return new SpringProfileArbiter(environment, profiles);
        }
    }
}
