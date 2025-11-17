package org.springframework.boot.autoconfigure.ssl;

import java.util.Set;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/ssl/SslBundleProperties.class */
public abstract class SslBundleProperties {
    private final Key key = new Key();
    private final Options options = new Options();
    private String protocol = "TLS";
    private boolean reloadOnUpdate;

    public Key getKey() {
        return this.key;
    }

    public Options getOptions() {
        return this.options;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public boolean isReloadOnUpdate() {
        return this.reloadOnUpdate;
    }

    public void setReloadOnUpdate(boolean reloadOnUpdate) {
        this.reloadOnUpdate = reloadOnUpdate;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/ssl/SslBundleProperties$Options.class */
    public static class Options {
        private Set<String> ciphers;
        private Set<String> enabledProtocols;

        public Set<String> getCiphers() {
            return this.ciphers;
        }

        public void setCiphers(Set<String> ciphers) {
            this.ciphers = ciphers;
        }

        public Set<String> getEnabledProtocols() {
            return this.enabledProtocols;
        }

        public void setEnabledProtocols(Set<String> enabledProtocols) {
            this.enabledProtocols = enabledProtocols;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/ssl/SslBundleProperties$Key.class */
    public static class Key {
        private String password;
        private String alias;

        public String getPassword() {
            return this.password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getAlias() {
            return this.alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }
    }
}
