package org.springframework.boot.logging.log4j2;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerContextAware;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.lookup.StrLookup;
import org.springframework.boot.autoconfigure.groovy.template.GroovyTemplateProperties;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

@Plugin(name = GroovyTemplateProperties.DEFAULT_REQUEST_CONTEXT_ATTRIBUTE, category = "Lookup")
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/log4j2/SpringEnvironmentLookup.class */
class SpringEnvironmentLookup implements LoggerContextAware, StrLookup {
    private volatile Environment environment;

    SpringEnvironmentLookup() {
    }

    public String lookup(LogEvent event, String key) {
        return lookup(key);
    }

    public String lookup(String key) {
        Assert.state(this.environment != null, "Unable to obtain Spring Environment from LoggerContext");
        return this.environment.getProperty(key);
    }

    public void setLoggerContext(LoggerContext loggerContext) {
        this.environment = Log4J2LoggingSystem.getEnvironment(loggerContext);
    }
}
