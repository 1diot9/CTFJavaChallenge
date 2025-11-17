package org.springframework.boot.autoconfigure.jmx;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.jmx.support.RegistrationPolicy;

@ConfigurationProperties(prefix = "spring.jmx")
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jmx/JmxProperties.class */
public class JmxProperties {
    private String defaultDomain;
    private boolean enabled = false;
    private boolean uniqueNames = false;
    private String server = "mbeanServer";
    private RegistrationPolicy registrationPolicy = RegistrationPolicy.FAIL_ON_EXISTING;

    public boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isUniqueNames() {
        return this.uniqueNames;
    }

    public void setUniqueNames(boolean uniqueNames) {
        this.uniqueNames = uniqueNames;
    }

    public String getServer() {
        return this.server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getDefaultDomain() {
        return this.defaultDomain;
    }

    public void setDefaultDomain(String defaultDomain) {
        this.defaultDomain = defaultDomain;
    }

    public RegistrationPolicy getRegistrationPolicy() {
        return this.registrationPolicy;
    }

    public void setRegistrationPolicy(RegistrationPolicy registrationPolicy) {
        this.registrationPolicy = registrationPolicy;
    }
}
