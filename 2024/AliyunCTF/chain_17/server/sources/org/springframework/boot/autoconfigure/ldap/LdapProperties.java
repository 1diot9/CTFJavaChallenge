package org.springframework.boot.autoconfigure.ldap;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

@ConfigurationProperties(prefix = "spring.ldap")
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/ldap/LdapProperties.class */
public class LdapProperties {
    private static final int DEFAULT_PORT = 389;
    private String[] urls;
    private String base;
    private String username;
    private String password;
    private Boolean anonymousReadOnly;
    private final Map<String, String> baseEnvironment = new HashMap();
    private final Template template = new Template();

    public String[] getUrls() {
        return this.urls;
    }

    public void setUrls(String[] urls) {
        this.urls = urls;
    }

    public String getBase() {
        return this.base;
    }

    public void setBase(String base) {
        this.base = base;
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

    public Boolean getAnonymousReadOnly() {
        return this.anonymousReadOnly;
    }

    public void setAnonymousReadOnly(Boolean anonymousReadOnly) {
        this.anonymousReadOnly = anonymousReadOnly;
    }

    public Map<String, String> getBaseEnvironment() {
        return this.baseEnvironment;
    }

    public Template getTemplate() {
        return this.template;
    }

    public String[] determineUrls(Environment environment) {
        if (ObjectUtils.isEmpty((Object[]) this.urls)) {
            return new String[]{"ldap://localhost:" + determinePort(environment)};
        }
        return this.urls;
    }

    private int determinePort(Environment environment) {
        Assert.notNull(environment, "Environment must not be null");
        String localPort = environment.getProperty("local.ldap.port");
        if (localPort != null) {
            return Integer.parseInt(localPort);
        }
        return DEFAULT_PORT;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/ldap/LdapProperties$Template.class */
    public static class Template {
        private boolean ignorePartialResultException = false;
        private boolean ignoreNameNotFoundException = false;
        private boolean ignoreSizeLimitExceededException = true;

        public boolean isIgnorePartialResultException() {
            return this.ignorePartialResultException;
        }

        public void setIgnorePartialResultException(boolean ignorePartialResultException) {
            this.ignorePartialResultException = ignorePartialResultException;
        }

        public boolean isIgnoreNameNotFoundException() {
            return this.ignoreNameNotFoundException;
        }

        public void setIgnoreNameNotFoundException(boolean ignoreNameNotFoundException) {
            this.ignoreNameNotFoundException = ignoreNameNotFoundException;
        }

        public boolean isIgnoreSizeLimitExceededException() {
            return this.ignoreSizeLimitExceededException;
        }

        public void setIgnoreSizeLimitExceededException(Boolean ignoreSizeLimitExceededException) {
            this.ignoreSizeLimitExceededException = ignoreSizeLimitExceededException.booleanValue();
        }
    }
}
