package org.springframework.boot.autoconfigure.ssl;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/ssl/PemSslBundleProperties.class */
public class PemSslBundleProperties extends SslBundleProperties {
    private final Store keystore = new Store();
    private final Store truststore = new Store();

    public Store getKeystore() {
        return this.keystore;
    }

    public Store getTruststore() {
        return this.truststore;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/ssl/PemSslBundleProperties$Store.class */
    public static class Store {
        private String type;
        private String certificate;
        private String privateKey;
        private String privateKeyPassword;
        private boolean verifyKeys;

        public String getType() {
            return this.type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCertificate() {
            return this.certificate;
        }

        public void setCertificate(String certificate) {
            this.certificate = certificate;
        }

        public String getPrivateKey() {
            return this.privateKey;
        }

        public void setPrivateKey(String privateKey) {
            this.privateKey = privateKey;
        }

        public String getPrivateKeyPassword() {
            return this.privateKeyPassword;
        }

        public void setPrivateKeyPassword(String privateKeyPassword) {
            this.privateKeyPassword = privateKeyPassword;
        }

        public boolean isVerifyKeys() {
            return this.verifyKeys;
        }

        public void setVerifyKeys(boolean verifyKeys) {
            this.verifyKeys = verifyKeys;
        }
    }
}
