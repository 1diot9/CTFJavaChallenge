package org.apache.tomcat.util.net.openssl;

import java.io.IOException;
import java.security.KeyStoreException;
import java.util.List;
import java.util.Set;
import javax.net.ssl.KeyManager;
import javax.net.ssl.X509KeyManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.SSLContext;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.apache.tomcat.util.net.SSLUtilBase;
import org.apache.tomcat.util.net.jsse.JSSEKeyManager;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/openssl/OpenSSLUtil.class */
public class OpenSSLUtil extends SSLUtilBase {
    private static final Log log = LogFactory.getLog((Class<?>) OpenSSLUtil.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) OpenSSLUtil.class);

    public OpenSSLUtil(SSLHostConfigCertificate certificate) {
        super(certificate);
    }

    @Override // org.apache.tomcat.util.net.SSLUtilBase
    protected Log getLog() {
        return log;
    }

    @Override // org.apache.tomcat.util.net.SSLUtilBase
    protected Set<String> getImplementedProtocols() {
        return OpenSSLEngine.IMPLEMENTED_PROTOCOLS_SET;
    }

    @Override // org.apache.tomcat.util.net.SSLUtilBase
    protected Set<String> getImplementedCiphers() {
        return OpenSSLEngine.AVAILABLE_CIPHER_SUITES;
    }

    @Override // org.apache.tomcat.util.net.SSLUtilBase
    protected boolean isTls13RenegAuthAvailable() {
        return true;
    }

    @Override // org.apache.tomcat.util.net.SSLUtilBase
    public SSLContext createSSLContextInternal(List<String> negotiableProtocols) throws Exception {
        return new OpenSSLContext(this.certificate, negotiableProtocols);
    }

    @Deprecated
    public static X509KeyManager chooseKeyManager(KeyManager[] managers) throws Exception {
        return chooseKeyManager(managers, true);
    }

    public static X509KeyManager chooseKeyManager(KeyManager[] managers, boolean throwOnMissing) throws Exception {
        if (managers == null) {
            return null;
        }
        for (KeyManager manager : managers) {
            if (manager instanceof JSSEKeyManager) {
                return (JSSEKeyManager) manager;
            }
        }
        for (KeyManager manager2 : managers) {
            if (manager2 instanceof X509KeyManager) {
                return (X509KeyManager) manager2;
            }
        }
        if (throwOnMissing) {
            throw new IllegalStateException(sm.getString("openssl.keyManagerMissing"));
        }
        log.warn(sm.getString("openssl.keyManagerMissing.warn"));
        return null;
    }

    @Override // org.apache.tomcat.util.net.SSLUtilBase, org.apache.tomcat.util.net.SSLUtil
    public KeyManager[] getKeyManagers() throws Exception {
        try {
            return super.getKeyManagers();
        } catch (IOException | KeyStoreException e) {
            if (this.certificate.getCertificateFile() != null) {
                String msg = sm.getString("openssl.nonJsseCertificate", this.certificate.getCertificateFile(), this.certificate.getCertificateKeyFile());
                if (log.isDebugEnabled()) {
                    log.info(msg, e);
                    return null;
                }
                log.info(msg);
                return null;
            }
            throw e;
        } catch (IllegalArgumentException e2) {
            String msg2 = sm.getString("openssl.nonJsseChain", this.certificate.getCertificateChainFile());
            if (log.isDebugEnabled()) {
                log.info(msg2, e2);
                return null;
            }
            log.info(msg2);
            return null;
        }
    }
}
