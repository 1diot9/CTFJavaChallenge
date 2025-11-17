package org.apache.tomcat.util.net.openssl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.ref.Cleaner;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jni.CertificateVerifier;
import org.apache.tomcat.jni.Pool;
import org.apache.tomcat.jni.SSL;
import org.apache.tomcat.jni.SSLConf;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.Constants;
import org.apache.tomcat.util.net.SSLContext;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.apache.tomcat.util.net.SSLUtilBase;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/openssl/OpenSSLContext.class */
public class OpenSSLContext implements SSLContext {
    private static final String defaultProtocol = "TLS";
    private static final String BEGIN_KEY = "-----BEGIN PRIVATE KEY-----\n";
    static final CertificateFactory X509_CERT_FACTORY;
    private static final Cleaner cleaner;
    private final SSLHostConfig sslHostConfig;
    private final SSLHostConfigCertificate certificate;
    private final List<String> negotiableProtocols;
    private OpenSSLSessionContext sessionContext;
    private X509TrustManager x509TrustManager;
    private String enabledProtocol;
    private boolean initialized = false;
    private final OpenSSLState state;
    private final Cleaner.Cleanable cleanable;
    private static final Log log = LogFactory.getLog((Class<?>) OpenSSLContext.class);
    private static final StringManager netSm = StringManager.getManager((Class<?>) AbstractEndpoint.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) OpenSSLContext.class);
    private static final Object END_KEY = "\n-----END PRIVATE KEY-----";

    static {
        try {
            X509_CERT_FACTORY = CertificateFactory.getInstance("X.509");
            cleaner = Cleaner.create();
        } catch (CertificateException e) {
            throw new IllegalStateException(sm.getString("openssl.X509FactoryError"), e);
        }
    }

    public OpenSSLContext(SSLHostConfigCertificate certificate, List<String> negotiableProtocols) throws SSLException {
        this.sslHostConfig = certificate.getSSLHostConfig();
        this.certificate = certificate;
        long aprPool = Pool.create(0L);
        long cctx = 0;
        try {
            try {
                OpenSSLConf openSslConf = this.sslHostConfig.getOpenSslConf();
                if (openSslConf != null) {
                    try {
                        if (log.isDebugEnabled()) {
                            log.debug(sm.getString("openssl.makeConf"));
                        }
                        cctx = SSLConf.make(aprPool, 58);
                    } catch (Exception e) {
                        throw new SSLException(sm.getString("openssl.errMakeConf"), e);
                    }
                }
                this.sslHostConfig.setOpenSslConfContext(Long.valueOf(cctx));
                int value = 0;
                for (String protocol : this.sslHostConfig.getEnabledProtocols()) {
                    if (!Constants.SSL_PROTO_SSLv2Hello.equalsIgnoreCase(protocol)) {
                        if (Constants.SSL_PROTO_SSLv2.equalsIgnoreCase(protocol)) {
                            value |= 1;
                        } else if (Constants.SSL_PROTO_SSLv3.equalsIgnoreCase(protocol)) {
                            value |= 2;
                        } else if (Constants.SSL_PROTO_TLSv1.equalsIgnoreCase(protocol)) {
                            value |= 4;
                        } else if (Constants.SSL_PROTO_TLSv1_1.equalsIgnoreCase(protocol)) {
                            value |= 8;
                        } else if (Constants.SSL_PROTO_TLSv1_2.equalsIgnoreCase(protocol)) {
                            value |= 16;
                        } else if (Constants.SSL_PROTO_TLSv1_3.equalsIgnoreCase(protocol)) {
                            value |= 32;
                        } else if (Constants.SSL_PROTO_ALL.equalsIgnoreCase(protocol)) {
                            value |= SSL.SSL_PROTOCOL_ALL;
                        } else {
                            throw new Exception(netSm.getString("endpoint.apr.invalidSslProtocol", protocol));
                        }
                    }
                }
                try {
                    long ctx = org.apache.tomcat.jni.SSLContext.make(aprPool, value, 1);
                    this.negotiableProtocols = negotiableProtocols;
                    this.state = new OpenSSLState(aprPool, cctx, ctx);
                    this.cleanable = cleaner.register(this, this.state);
                    if (1 == 0) {
                        destroy();
                    }
                } catch (Exception e2) {
                    throw new Exception(netSm.getString("endpoint.apr.failSslContextMake"), e2);
                }
            } catch (Exception e3) {
                throw new SSLException(sm.getString("openssl.errorSSLCtxInit"), e3);
            }
        } catch (Throwable th) {
            this.state = new OpenSSLState(aprPool, 0L, 0L);
            this.cleanable = cleaner.register(this, this.state);
            if (0 == 0) {
                destroy();
            }
            throw th;
        }
    }

    public String getEnabledProtocol() {
        return this.enabledProtocol;
    }

    public void setEnabledProtocol(String protocol) {
        this.enabledProtocol = protocol == null ? "TLS" : protocol;
    }

    @Override // org.apache.tomcat.util.net.SSLContext
    public synchronized void destroy() {
        this.cleanable.clean();
    }

    protected static boolean checkConf(OpenSSLConf conf, long cctx) throws Exception {
        boolean result = true;
        for (OpenSSLConfCmd command : conf.getCommands()) {
            String name = command.getName();
            String value = command.getValue();
            if (name == null) {
                log.error(sm.getString("opensslconf.noCommandName", value));
                result = false;
            } else {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("opensslconf.checkCommand", name, value));
                }
                try {
                    int rc = SSLConf.check(cctx, name, value);
                    if (rc <= 0) {
                        log.error(sm.getString("opensslconf.failedCommand", name, value, Integer.toString(rc)));
                        result = false;
                    } else if (log.isDebugEnabled()) {
                        log.debug(sm.getString("opensslconf.resultCommand", name, value, Integer.toString(rc)));
                    }
                } catch (Exception e) {
                    log.error(sm.getString("opensslconf.checkFailed"));
                    return false;
                }
            }
        }
        if (!result) {
            log.error(sm.getString("opensslconf.checkFailed"));
        }
        return result;
    }

    protected static boolean applyConf(OpenSSLConf conf, long cctx, long ctx) throws Exception {
        boolean result = true;
        SSLConf.assign(cctx, ctx);
        for (OpenSSLConfCmd command : conf.getCommands()) {
            String name = command.getName();
            String value = command.getValue();
            if (name == null) {
                log.error(sm.getString("opensslconf.noCommandName", value));
                result = false;
            } else {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("opensslconf.applyCommand", name, value));
                }
                try {
                    int rc = SSLConf.apply(cctx, name, value);
                    if (rc <= 0) {
                        log.error(sm.getString("opensslconf.failedCommand", name, value, Integer.toString(rc)));
                        result = false;
                    } else if (log.isDebugEnabled()) {
                        log.debug(sm.getString("opensslconf.resultCommand", name, value, Integer.toString(rc)));
                    }
                } catch (Exception e) {
                    log.error(sm.getString("opensslconf.applyFailed"));
                    return false;
                }
            }
        }
        int rc2 = SSLConf.finish(cctx);
        if (rc2 <= 0) {
            log.error(sm.getString("opensslconf.finishFailed", Integer.toString(rc2)));
            result = false;
        }
        if (!result) {
            log.error(sm.getString("opensslconf.applyFailed"));
        }
        return result;
    }

    @Override // org.apache.tomcat.util.net.SSLContext
    public synchronized void init(KeyManager[] kms, TrustManager[] tms, SecureRandom sr) {
        OpenSSLConf openSslConf;
        if (this.initialized) {
            log.warn(sm.getString("openssl.doubleInit"));
            return;
        }
        try {
            if (this.sslHostConfig.getInsecureRenegotiation()) {
                org.apache.tomcat.jni.SSLContext.setOptions(this.state.ctx, 262144);
            } else {
                org.apache.tomcat.jni.SSLContext.clearOptions(this.state.ctx, 262144);
            }
            if (this.sslHostConfig.getHonorCipherOrder()) {
                org.apache.tomcat.jni.SSLContext.setOptions(this.state.ctx, SSL.SSL_OP_CIPHER_SERVER_PREFERENCE);
            } else {
                org.apache.tomcat.jni.SSLContext.clearOptions(this.state.ctx, SSL.SSL_OP_CIPHER_SERVER_PREFERENCE);
            }
            if (this.sslHostConfig.getDisableCompression()) {
                org.apache.tomcat.jni.SSLContext.setOptions(this.state.ctx, 131072);
            } else {
                org.apache.tomcat.jni.SSLContext.clearOptions(this.state.ctx, 131072);
            }
            if (this.sslHostConfig.getDisableSessionTickets()) {
                org.apache.tomcat.jni.SSLContext.setOptions(this.state.ctx, 16384);
            } else {
                org.apache.tomcat.jni.SSLContext.clearOptions(this.state.ctx, 16384);
            }
            org.apache.tomcat.jni.SSLContext.setCipherSuite(this.state.ctx, this.sslHostConfig.getCiphers());
            this.certificate.setCertificateKeyManager(OpenSSLUtil.chooseKeyManager(kms, this.certificate.getCertificateFile() == null));
            addCertificate(this.certificate);
            int value = 0;
            switch (this.sslHostConfig.getCertificateVerification()) {
                case NONE:
                    value = 0;
                    break;
                case OPTIONAL:
                    value = 1;
                    break;
                case OPTIONAL_NO_CA:
                    value = 3;
                    break;
                case REQUIRED:
                    value = 2;
                    break;
            }
            org.apache.tomcat.jni.SSLContext.setVerify(this.state.ctx, value, this.sslHostConfig.getCertificateVerificationDepth());
            if (tms != null) {
                this.x509TrustManager = chooseTrustManager(tms);
                org.apache.tomcat.jni.SSLContext.setCertVerifyCallback(this.state.ctx, new CertificateVerifier() { // from class: org.apache.tomcat.util.net.openssl.OpenSSLContext.1
                    @Override // org.apache.tomcat.jni.CertificateVerifier
                    public boolean verify(long ssl, byte[][] chain, String auth) {
                        X509Certificate[] peerCerts = OpenSSLContext.certificates(chain);
                        try {
                            OpenSSLContext.this.x509TrustManager.checkClientTrusted(peerCerts, auth);
                            return true;
                        } catch (Exception e) {
                            OpenSSLContext.log.debug(OpenSSLContext.sm.getString("openssl.certificateVerificationFailed"), e);
                            return false;
                        }
                    }
                });
                for (X509Certificate caCert : this.x509TrustManager.getAcceptedIssuers()) {
                    org.apache.tomcat.jni.SSLContext.addClientCACertificateRaw(this.state.ctx, caCert.getEncoded());
                    if (log.isDebugEnabled()) {
                        log.debug(sm.getString("openssl.addedClientCaCert", caCert.toString()));
                    }
                }
            } else {
                org.apache.tomcat.jni.SSLContext.setCACertificate(this.state.ctx, SSLHostConfig.adjustRelativePath(this.sslHostConfig.getCaCertificateFile()), SSLHostConfig.adjustRelativePath(this.sslHostConfig.getCaCertificatePath()));
            }
            if (this.negotiableProtocols != null && this.negotiableProtocols.size() > 0) {
                List<String> protocols = new ArrayList<>(this.negotiableProtocols);
                protocols.add("http/1.1");
                String[] protocolsArray = (String[]) protocols.toArray(new String[0]);
                org.apache.tomcat.jni.SSLContext.setAlpnProtos(this.state.ctx, protocolsArray, 0);
            }
            openSslConf = this.sslHostConfig.getOpenSslConf();
        } catch (Exception e) {
            log.warn(sm.getString("openssl.errorSSLCtxInit"), e);
            destroy();
            return;
        }
        if (openSslConf != null && this.state.cctx != 0) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("openssl.checkConf"));
            }
            try {
                if (!checkConf(openSslConf, this.state.cctx)) {
                    log.error(sm.getString("openssl.errCheckConf"));
                    throw new Exception(sm.getString("openssl.errCheckConf"));
                }
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("openssl.applyConf"));
                }
                try {
                    if (!applyConf(openSslConf, this.state.cctx, this.state.ctx)) {
                        log.error(sm.getString("openssl.errApplyConf"));
                        throw new SSLException(sm.getString("openssl.errApplyConf"));
                    }
                    int opts = org.apache.tomcat.jni.SSLContext.getOptions(this.state.ctx);
                    List<String> enabled = new ArrayList<>();
                    enabled.add(Constants.SSL_PROTO_SSLv2Hello);
                    if ((opts & SSL.SSL_OP_NO_TLSv1) == 0) {
                        enabled.add(Constants.SSL_PROTO_TLSv1);
                    }
                    if ((opts & SSL.SSL_OP_NO_TLSv1_1) == 0) {
                        enabled.add(Constants.SSL_PROTO_TLSv1_1);
                    }
                    if ((opts & SSL.SSL_OP_NO_TLSv1_2) == 0) {
                        enabled.add(Constants.SSL_PROTO_TLSv1_2);
                    }
                    if ((opts & SSL.SSL_OP_NO_SSLv2) == 0) {
                        enabled.add(Constants.SSL_PROTO_SSLv2);
                    }
                    if ((opts & SSL.SSL_OP_NO_SSLv3) == 0) {
                        enabled.add(Constants.SSL_PROTO_SSLv3);
                    }
                    this.sslHostConfig.setEnabledProtocols((String[]) enabled.toArray(new String[0]));
                    this.sslHostConfig.setEnabledCiphers(org.apache.tomcat.jni.SSLContext.getCiphers(this.state.ctx));
                } catch (Exception e2) {
                    throw new SSLException(sm.getString("openssl.errApplyConf"), e2);
                }
            } catch (Exception e3) {
                throw new Exception(sm.getString("openssl.errCheckConf"), e3);
            }
            log.warn(sm.getString("openssl.errorSSLCtxInit"), e);
            destroy();
            return;
        }
        this.sessionContext = new OpenSSLSessionContext(this);
        this.sessionContext.setSessionIdContext(org.apache.tomcat.jni.SSLContext.DEFAULT_SESSION_ID_CONTEXT);
        this.sslHostConfig.setOpenSslContext(Long.valueOf(this.state.ctx));
        this.initialized = true;
    }

    public void addCertificate(SSLHostConfigCertificate certificate) throws Exception {
        String passwordToUse;
        if (certificate.getCertificateFile() != null) {
            if (certificate.getCertificateKeyPasswordFile() != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(SSLHostConfig.adjustRelativePath(certificate.getCertificateKeyPasswordFile())), StandardCharsets.UTF_8));
                try {
                    passwordToUse = reader.readLine();
                    reader.close();
                } catch (Throwable th) {
                    try {
                        reader.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                    throw th;
                }
            } else {
                passwordToUse = certificate.getCertificateKeyPassword();
            }
            org.apache.tomcat.jni.SSLContext.setCertificate(this.state.ctx, SSLHostConfig.adjustRelativePath(certificate.getCertificateFile()), SSLHostConfig.adjustRelativePath(certificate.getCertificateKeyFile()), passwordToUse, getCertificateIndex(certificate));
            org.apache.tomcat.jni.SSLContext.setCertificateChainFile(this.state.ctx, SSLHostConfig.adjustRelativePath(certificate.getCertificateChainFile()), false);
            org.apache.tomcat.jni.SSLContext.setCARevocation(this.state.ctx, SSLHostConfig.adjustRelativePath(this.sslHostConfig.getCertificateRevocationListFile()), SSLHostConfig.adjustRelativePath(this.sslHostConfig.getCertificateRevocationListPath()));
            return;
        }
        String alias = certificate.getCertificateKeyAlias();
        X509KeyManager x509KeyManager = certificate.getCertificateKeyManager();
        if (alias == null) {
            alias = SSLUtilBase.DEFAULT_KEY_ALIAS;
        }
        X509Certificate[] chain = x509KeyManager.getCertificateChain(alias);
        if (chain == null) {
            alias = findAlias(x509KeyManager, certificate);
            chain = x509KeyManager.getCertificateChain(alias);
        }
        PrivateKey key = x509KeyManager.getPrivateKey(alias);
        org.apache.tomcat.jni.SSLContext.setCertificateRaw(this.state.ctx, chain[0].getEncoded(), (BEGIN_KEY + Base64.getMimeEncoder(64, new byte[]{10}).encodeToString(key.getEncoded()) + END_KEY).getBytes(StandardCharsets.US_ASCII), getCertificateIndex(certificate));
        for (int i = 1; i < chain.length; i++) {
            org.apache.tomcat.jni.SSLContext.addChainCertificateRaw(this.state.ctx, chain[i].getEncoded());
        }
    }

    private static int getCertificateIndex(SSLHostConfigCertificate certificate) {
        int result;
        if (certificate.getType() == SSLHostConfigCertificate.Type.RSA || certificate.getType() == SSLHostConfigCertificate.Type.UNDEFINED) {
            result = 0;
        } else if (certificate.getType() == SSLHostConfigCertificate.Type.EC) {
            result = 3;
        } else if (certificate.getType() == SSLHostConfigCertificate.Type.DSA) {
            result = 1;
        } else {
            result = 4;
        }
        return result;
    }

    private static String findAlias(X509KeyManager keyManager, SSLHostConfigCertificate certificate) {
        SSLHostConfigCertificate.Type type = certificate.getType();
        String result = null;
        List<SSLHostConfigCertificate.Type> candidateTypes = new ArrayList<>();
        if (SSLHostConfigCertificate.Type.UNDEFINED.equals(type)) {
            candidateTypes.addAll(Arrays.asList(SSLHostConfigCertificate.Type.values()));
            candidateTypes.remove(SSLHostConfigCertificate.Type.UNDEFINED);
        } else {
            candidateTypes.add(type);
        }
        Iterator<SSLHostConfigCertificate.Type> iter = candidateTypes.iterator();
        while (result == null && iter.hasNext()) {
            result = keyManager.chooseServerAlias(iter.next().toString(), null, null);
        }
        return result;
    }

    private static X509TrustManager chooseTrustManager(TrustManager[] managers) {
        for (TrustManager m : managers) {
            if (m instanceof X509TrustManager) {
                return (X509TrustManager) m;
            }
        }
        throw new IllegalStateException(sm.getString("openssl.trustManagerMissing"));
    }

    private static X509Certificate[] certificates(byte[][] chain) {
        X509Certificate[] peerCerts = new X509Certificate[chain.length];
        for (int i = 0; i < peerCerts.length; i++) {
            peerCerts[i] = new OpenSSLX509Certificate(chain[i]);
        }
        return peerCerts;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long getSSLContextID() {
        return this.state.ctx;
    }

    @Override // org.apache.tomcat.util.net.SSLContext
    public SSLSessionContext getServerSessionContext() {
        return this.sessionContext;
    }

    @Override // org.apache.tomcat.util.net.SSLContext
    public SSLEngine createSSLEngine() {
        return new OpenSSLEngine(cleaner, this.state.ctx, "TLS", false, this.sessionContext, this.negotiableProtocols != null && this.negotiableProtocols.size() > 0, this.initialized, this.sslHostConfig.getCertificateVerificationDepth(), this.sslHostConfig.getCertificateVerification() == SSLHostConfig.CertificateVerification.OPTIONAL_NO_CA);
    }

    @Override // org.apache.tomcat.util.net.SSLContext
    public SSLServerSocketFactory getServerSocketFactory() {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.tomcat.util.net.SSLContext
    public SSLParameters getSupportedSSLParameters() {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.tomcat.util.net.SSLContext
    public X509Certificate[] getCertificateChain(String alias) {
        X509Certificate[] chain = null;
        X509KeyManager x509KeyManager = this.certificate.getCertificateKeyManager();
        if (x509KeyManager != null) {
            if (alias == null) {
                alias = SSLUtilBase.DEFAULT_KEY_ALIAS;
            }
            chain = x509KeyManager.getCertificateChain(alias);
            if (chain == null) {
                chain = x509KeyManager.getCertificateChain(findAlias(x509KeyManager, this.certificate));
            }
        }
        return chain;
    }

    @Override // org.apache.tomcat.util.net.SSLContext
    public X509Certificate[] getAcceptedIssuers() {
        X509Certificate[] acceptedCerts = null;
        if (this.x509TrustManager != null) {
            acceptedCerts = this.x509TrustManager.getAcceptedIssuers();
        }
        return acceptedCerts;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/openssl/OpenSSLContext$OpenSSLState.class */
    public static class OpenSSLState implements Runnable {
        final long aprPool;
        final long cctx;
        final long ctx;

        private OpenSSLState(long aprPool, long cctx, long ctx) {
            this.aprPool = aprPool;
            this.cctx = cctx;
            this.ctx = ctx;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.ctx != 0) {
                org.apache.tomcat.jni.SSLContext.free(this.ctx);
            }
            if (this.cctx != 0) {
                SSLConf.free(this.cctx);
            }
            if (this.aprPool != 0) {
                Pool.destroy(this.aprPool);
            }
        }
    }
}
