package jakarta.security.auth.message.callback;

import java.security.cert.CertStore;
import javax.security.auth.callback.Callback;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/security/auth/message/callback/CertStoreCallback.class */
public class CertStoreCallback implements Callback {
    private CertStore certStore;

    public void setCertStore(CertStore certStore) {
        this.certStore = certStore;
    }

    public CertStore getCertStore() {
        return this.certStore;
    }
}
