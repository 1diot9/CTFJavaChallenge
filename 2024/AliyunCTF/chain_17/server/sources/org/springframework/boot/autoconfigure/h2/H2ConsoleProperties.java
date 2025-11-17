package org.springframework.boot.autoconfigure.h2;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

@ConfigurationProperties(prefix = "spring.h2.console")
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/h2/H2ConsoleProperties.class */
public class H2ConsoleProperties {
    private String path = "/h2-console";
    private boolean enabled = false;
    private final Settings settings = new Settings();

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        Assert.notNull(path, "Path must not be null");
        Assert.isTrue(path.length() > 1, "Path must have length greater than 1");
        Assert.isTrue(path.startsWith("/"), "Path must start with '/'");
        this.path = path;
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Settings getSettings() {
        return this.settings;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/h2/H2ConsoleProperties$Settings.class */
    public static class Settings {
        private boolean trace = false;
        private boolean webAllowOthers = false;
        private String webAdminPassword;

        public boolean isTrace() {
            return this.trace;
        }

        public void setTrace(boolean trace) {
            this.trace = trace;
        }

        public boolean isWebAllowOthers() {
            return this.webAllowOthers;
        }

        public void setWebAllowOthers(boolean webAllowOthers) {
            this.webAllowOthers = webAllowOthers;
        }

        public String getWebAdminPassword() {
            return this.webAdminPassword;
        }

        public void setWebAdminPassword(String webAdminPassword) {
            this.webAdminPassword = webAdminPassword;
        }
    }
}
