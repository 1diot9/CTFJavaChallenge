package org.springframework.boot.logging.log4j2;

import org.apache.logging.log4j.util.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/log4j2/SpringEnvironmentPropertySource.class */
class SpringEnvironmentPropertySource implements PropertySource {
    private static final int PRIORITY = -100;
    private final Environment environment;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SpringEnvironmentPropertySource(Environment environment) {
        Assert.notNull(environment, "Environment must not be null");
        this.environment = environment;
    }

    @Override // org.apache.logging.log4j.util.PropertySource
    public int getPriority() {
        return -100;
    }

    @Override // org.apache.logging.log4j.util.PropertySource
    public String getProperty(String key) {
        return this.environment.getProperty(key);
    }

    @Override // org.apache.logging.log4j.util.PropertySource
    public boolean containsProperty(String key) {
        return this.environment.containsProperty(key);
    }
}
