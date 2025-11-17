package org.springframework.boot.autoconfigure.ssl;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/ssl/JksSslBundleProperties.class */
public class JksSslBundleProperties extends SslBundleProperties {
    private final Store keystore = new Store();
    private final Store truststore = new Store();

    public Store getKeystore() {
        return this.keystore;
    }

    public Store getTruststore() {
        return this.truststore;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/ssl/JksSslBundleProperties$Store.class */
    public static class Store {
        private String type;
        private String provider;
        private String location;
        private String password;

        public String getType() {
            return this.type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getProvider() {
            return this.provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public String getLocation() {
            return this.location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getPassword() {
            return this.password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
