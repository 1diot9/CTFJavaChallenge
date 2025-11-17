package jakarta.security.auth.message.callback;

import java.security.KeyStore;
import javax.security.auth.callback.Callback;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/security/auth/message/callback/TrustStoreCallback.class */
public class TrustStoreCallback implements Callback {
    private KeyStore trustStore;

    public void setTrustStore(KeyStore trustStore) {
        this.trustStore = trustStore;
    }

    public KeyStore getTrustStore() {
        return this.trustStore;
    }
}
