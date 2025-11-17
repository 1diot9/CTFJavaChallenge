package org.apache.tomcat.util.net;

import ch.qos.logback.core.net.ssl.SSL;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.DomainLoadStoreParameter;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CertPathParameters;
import java.security.cert.CertStore;
import java.security.cert.CertStoreParameters;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.net.ssl.CertPathTrustManagerParameters;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.file.ConfigFileLoader;
import org.apache.tomcat.util.net.jsse.JSSEKeyManager;
import org.apache.tomcat.util.net.jsse.PEMFile;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.security.KeyStoreUtil;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/SSLUtilBase.class */
public abstract class SSLUtilBase implements SSLUtil {
    private static final Log log = LogFactory.getLog((Class<?>) SSLUtilBase.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) SSLUtilBase.class);
    public static final String DEFAULT_KEY_ALIAS = "tomcat";
    protected final SSLHostConfig sslHostConfig;
    protected final SSLHostConfigCertificate certificate;
    private final String[] enabledProtocols;
    private final String[] enabledCiphers;

    protected abstract Set<String> getImplementedProtocols();

    protected abstract Set<String> getImplementedCiphers();

    protected abstract Log getLog();

    protected abstract boolean isTls13RenegAuthAvailable();

    protected abstract SSLContext createSSLContextInternal(List<String> list) throws Exception;

    /* JADX INFO: Access modifiers changed from: protected */
    public SSLUtilBase(SSLHostConfigCertificate certificate) {
        this(certificate, true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public SSLUtilBase(SSLHostConfigCertificate certificate, boolean warnTls13) {
        this.certificate = certificate;
        this.sslHostConfig = certificate.getSSLHostConfig();
        Set<String> configuredProtocols = this.sslHostConfig.getProtocols();
        Set<String> implementedProtocols = getImplementedProtocols();
        if (!implementedProtocols.contains(Constants.SSL_PROTO_TLSv1_3) && !this.sslHostConfig.isExplicitlyRequestedProtocol(Constants.SSL_PROTO_TLSv1_3)) {
            configuredProtocols.remove(Constants.SSL_PROTO_TLSv1_3);
        }
        if (!implementedProtocols.contains(Constants.SSL_PROTO_SSLv2Hello) && !this.sslHostConfig.isExplicitlyRequestedProtocol(Constants.SSL_PROTO_SSLv2Hello)) {
            configuredProtocols.remove(Constants.SSL_PROTO_SSLv2Hello);
        }
        List<String> enabledProtocols = getEnabled("protocols", getLog(), warnTls13, configuredProtocols, implementedProtocols);
        if (enabledProtocols.contains(Constants.SSL_PROTO_SSLv3)) {
            log.warn(sm.getString("sslUtilBase.ssl3"));
        }
        this.enabledProtocols = (String[]) enabledProtocols.toArray(new String[0]);
        if (enabledProtocols.contains(Constants.SSL_PROTO_TLSv1_3) && this.sslHostConfig.getCertificateVerification().isOptional() && !isTls13RenegAuthAvailable() && warnTls13) {
            log.warn(sm.getString("sslUtilBase.tls13.auth"));
        }
        this.sslHostConfig.setTls13RenegotiationAvailable(isTls13RenegAuthAvailable());
        if (this.sslHostConfig.getCiphers().startsWith("PROFILE=")) {
            this.enabledCiphers = new String[0];
            return;
        }
        boolean warnOnSkip = !this.sslHostConfig.getCiphers().equals(SSLHostConfig.DEFAULT_TLS_CIPHERS);
        List<String> configuredCiphers = this.sslHostConfig.getJsseCipherNames();
        Set<String> implementedCiphers = getImplementedCiphers();
        List<String> enabledCiphers = getEnabled("ciphers", getLog(), warnOnSkip, configuredCiphers, implementedCiphers);
        this.enabledCiphers = (String[]) enabledCiphers.toArray(new String[0]);
    }

    static <T> List<T> getEnabled(String name, Log log2, boolean warnOnSkip, Collection<T> configured, Collection<T> implemented) {
        List<T> enabled = new ArrayList<>();
        if (implemented.size() == 0) {
            enabled.addAll(configured);
        } else {
            enabled.addAll(configured);
            enabled.retainAll(implemented);
            if (enabled.isEmpty()) {
                throw new IllegalArgumentException(sm.getString("sslUtilBase.noneSupported", name, configured));
            }
            if (log2.isDebugEnabled()) {
                log2.debug(sm.getString("sslUtilBase.active", name, enabled));
            }
            if ((log2.isDebugEnabled() || warnOnSkip) && enabled.size() != configured.size()) {
                List<T> skipped = new ArrayList<>((Collection<? extends T>) configured);
                skipped.removeAll(enabled);
                String msg = sm.getString("sslUtilBase.skipped", name, skipped);
                if (warnOnSkip) {
                    log2.warn(msg);
                } else {
                    log2.debug(msg);
                }
            }
        }
        return enabled;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static KeyStore getStore(String type, String provider, String path, String pass, String passFile) throws IOException {
        KeyStore ks;
        String passToUse;
        InputStream istream = null;
        try {
            try {
                try {
                    if (provider == null) {
                        ks = KeyStore.getInstance(type);
                    } else {
                        ks = KeyStore.getInstance(type, provider);
                    }
                    if ("DKS".equalsIgnoreCase(type)) {
                        URI uri = ConfigFileLoader.getSource().getURI(path);
                        ks.load(new DomainLoadStoreParameter(uri, Collections.emptyMap()));
                    } else {
                        if (!"PKCS11".equalsIgnoreCase(type) && !path.isEmpty() && !"NONE".equalsIgnoreCase(path)) {
                            istream = ConfigFileLoader.getSource().getResource(path).getInputStream();
                        }
                        char[] storePass = null;
                        if (passFile != null) {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(ConfigFileLoader.getSource().getResource(passFile).getInputStream(), StandardCharsets.UTF_8));
                            try {
                                passToUse = reader.readLine();
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
                            passToUse = pass;
                        }
                        if (passToUse != null && (!"".equals(passToUse) || SSL.DEFAULT_KEYSTORE_TYPE.equalsIgnoreCase(type) || "PKCS12".equalsIgnoreCase(type))) {
                            storePass = passToUse.toCharArray();
                        }
                        KeyStoreUtil.load(ks, istream, storePass);
                    }
                    if (istream != null) {
                        try {
                            istream.close();
                        } catch (IOException e) {
                        }
                    }
                    return ks;
                } catch (Exception ex) {
                    String msg = sm.getString("sslUtilBase.keystore_load_failed", type, path, ex.getMessage());
                    log.error(msg, ex);
                    throw new IOException(msg);
                }
            } catch (IOException ioe) {
                throw ioe;
            }
        } catch (Throwable th3) {
            if (0 != 0) {
                try {
                    istream.close();
                } catch (IOException e2) {
                }
            }
            throw th3;
        }
    }

    @Override // org.apache.tomcat.util.net.SSLUtil
    public final SSLContext createSSLContext(List<String> negotiableProtocols) throws Exception {
        SSLContext sslContext = createSSLContextInternal(negotiableProtocols);
        sslContext.init(getKeyManagers(), getTrustManagers(), null);
        SSLSessionContext sessionContext = sslContext.getServerSessionContext();
        if (sessionContext != null) {
            configureSessionContext(sessionContext);
        }
        return sslContext;
    }

    @Override // org.apache.tomcat.util.net.SSLUtil
    public void configureSessionContext(SSLSessionContext sslSessionContext) {
        if (this.sslHostConfig.getSessionCacheSize() >= 0) {
            sslSessionContext.setSessionCacheSize(this.sslHostConfig.getSessionCacheSize());
        }
        if (this.sslHostConfig.getSessionTimeout() >= 0) {
            sslSessionContext.setSessionTimeout(this.sslHostConfig.getSessionTimeout());
        }
    }

    @Override // org.apache.tomcat.util.net.SSLUtil
    public KeyManager[] getKeyManagers() throws Exception {
        String keyPassToUse;
        String keyAlias = this.certificate.getCertificateKeyAlias();
        String algorithm = this.sslHostConfig.getKeyManagerAlgorithm();
        String keyPassFile = this.certificate.getCertificateKeyPasswordFile();
        String keyPass = this.certificate.getCertificateKeyPassword();
        if (keyPassFile == null) {
            keyPassFile = this.certificate.getCertificateKeystorePasswordFile();
        }
        if (keyPass == null) {
            keyPass = this.certificate.getCertificateKeystorePassword();
        }
        KeyStore ks = this.certificate.getCertificateKeystore();
        KeyStore ksUsed = ks;
        char[] keyPassArray = null;
        if (keyPassFile != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(ConfigFileLoader.getSource().getResource(keyPassFile).getInputStream(), StandardCharsets.UTF_8));
            try {
                keyPassToUse = reader.readLine();
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
            keyPassToUse = keyPass;
        }
        if (keyPassToUse != null) {
            keyPassArray = keyPassToUse.toCharArray();
        }
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
        if (kmf.getProvider().getInfo().contains("FIPS")) {
            if (keyAlias != null) {
                log.warn(sm.getString("sslUtilBase.aliasIgnored", keyAlias));
            }
            kmf.init(ksUsed, keyPassArray);
            return kmf.getKeyManagers();
        }
        if (ks == null) {
            if (this.certificate.getCertificateFile() == null) {
                throw new IOException(sm.getString("sslUtilBase.noCertFile"));
            }
            PEMFile privateKeyFile = new PEMFile(this.certificate.getCertificateKeyFile() != null ? this.certificate.getCertificateKeyFile() : this.certificate.getCertificateFile(), keyPass, keyPassFile, (String) null);
            PEMFile certificateFile = new PEMFile(this.certificate.getCertificateFile());
            Collection<Certificate> chain = new ArrayList<>(certificateFile.getCertificates());
            if (this.certificate.getCertificateChainFile() != null) {
                PEMFile certificateChainFile = new PEMFile(this.certificate.getCertificateChainFile());
                chain.addAll(certificateChainFile.getCertificates());
            }
            if (keyAlias == null) {
                keyAlias = DEFAULT_KEY_ALIAS;
            }
            ksUsed = KeyStore.getInstance(SSL.DEFAULT_KEYSTORE_TYPE);
            ksUsed.load(null, null);
            ksUsed.setKeyEntry(keyAlias, privateKeyFile.getPrivateKey(), keyPassArray, (Certificate[]) chain.toArray(new Certificate[0]));
        } else {
            if (keyAlias != null && !ks.isKeyEntry(keyAlias)) {
                throw new IOException(sm.getString("sslUtilBase.alias_no_key_entry", keyAlias));
            }
            if (keyAlias == null) {
                Enumeration<String> aliases = ks.aliases();
                if (!aliases.hasMoreElements()) {
                    throw new IOException(sm.getString("sslUtilBase.noKeys"));
                }
                while (aliases.hasMoreElements() && keyAlias == null) {
                    keyAlias = aliases.nextElement();
                    if (!ks.isKeyEntry(keyAlias)) {
                        keyAlias = null;
                    }
                }
                if (keyAlias == null) {
                    throw new IOException(sm.getString("sslUtilBase.alias_no_key_entry", null));
                }
            }
            Key k = ks.getKey(keyAlias, keyPassArray);
            if (k != null && !"DKS".equalsIgnoreCase(this.certificate.getCertificateKeystoreType()) && "PKCS#8".equalsIgnoreCase(k.getFormat())) {
                String provider = this.certificate.getCertificateKeystoreProvider();
                if (provider == null) {
                    ksUsed = KeyStore.getInstance(this.certificate.getCertificateKeystoreType());
                } else {
                    ksUsed = KeyStore.getInstance(this.certificate.getCertificateKeystoreType(), provider);
                }
                ksUsed.load(null, null);
                ksUsed.setKeyEntry(keyAlias, k, keyPassArray, ks.getCertificateChain(keyAlias));
            }
        }
        kmf.init(ksUsed, keyPassArray);
        KeyManager[] kms = kmf.getKeyManagers();
        if (kms != null && ksUsed == ks) {
            String alias = keyAlias;
            if (SSL.DEFAULT_KEYSTORE_TYPE.equals(this.certificate.getCertificateKeystoreType())) {
                alias = alias.toLowerCase(Locale.ENGLISH);
            }
            for (int i = 0; i < kms.length; i++) {
                kms[i] = new JSSEKeyManager((X509KeyManager) kms[i], alias);
            }
        }
        return kms;
    }

    @Override // org.apache.tomcat.util.net.SSLUtil
    public String[] getEnabledProtocols() {
        return this.enabledProtocols;
    }

    @Override // org.apache.tomcat.util.net.SSLUtil
    public String[] getEnabledCiphers() {
        return this.enabledCiphers;
    }

    @Override // org.apache.tomcat.util.net.SSLUtil
    public TrustManager[] getTrustManagers() throws Exception {
        String className = this.sslHostConfig.getTrustManagerClassName();
        if (className != null && className.length() > 0) {
            ClassLoader classLoader = getClass().getClassLoader();
            Class<?> clazz = classLoader.loadClass(className);
            if (!TrustManager.class.isAssignableFrom(clazz)) {
                throw new InstantiationException(sm.getString("sslUtilBase.invalidTrustManagerClassName", className));
            }
            Object trustManagerObject = clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            TrustManager trustManager = (TrustManager) trustManagerObject;
            return new TrustManager[]{trustManager};
        }
        TrustManager[] tms = null;
        KeyStore trustStore = this.sslHostConfig.getTruststore();
        if (trustStore != null) {
            checkTrustStoreEntries(trustStore);
            String algorithm = this.sslHostConfig.getTruststoreAlgorithm();
            String crlf = this.sslHostConfig.getCertificateRevocationListFile();
            boolean revocationEnabled = this.sslHostConfig.getRevocationEnabled();
            if ("PKIX".equalsIgnoreCase(algorithm)) {
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm);
                CertPathParameters params = getParameters(crlf, trustStore, revocationEnabled);
                ManagerFactoryParameters mfp = new CertPathTrustManagerParameters(params);
                tmf.init(mfp);
                tms = tmf.getTrustManagers();
            } else {
                TrustManagerFactory tmf2 = TrustManagerFactory.getInstance(algorithm);
                tmf2.init(trustStore);
                tms = tmf2.getTrustManagers();
                if (crlf != null && crlf.length() > 0) {
                    throw new CRLException(sm.getString("sslUtilBase.noCrlSupport", algorithm));
                }
                if (this.sslHostConfig.isCertificateVerificationDepthConfigured()) {
                    log.warn(sm.getString("sslUtilBase.noVerificationDepth", algorithm));
                }
            }
        }
        return tms;
    }

    private void checkTrustStoreEntries(KeyStore trustStore) throws Exception {
        Enumeration<String> aliases = trustStore.aliases();
        if (aliases != null) {
            Date now = new Date();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                if (trustStore.isCertificateEntry(alias)) {
                    Certificate cert = trustStore.getCertificate(alias);
                    if (cert instanceof X509Certificate) {
                        try {
                            ((X509Certificate) cert).checkValidity(now);
                        } catch (CertificateExpiredException | CertificateNotYetValidException e) {
                            String msg = sm.getString("sslUtilBase.trustedCertNotValid", alias, ((X509Certificate) cert).getSubjectX500Principal(), e.getMessage());
                            if (log.isDebugEnabled()) {
                                log.warn(msg, e);
                            } else {
                                log.warn(msg);
                            }
                        }
                    } else if (log.isDebugEnabled()) {
                        log.debug(sm.getString("sslUtilBase.trustedCertNotChecked", alias));
                    }
                }
            }
        }
    }

    protected CertPathParameters getParameters(String crlf, KeyStore trustStore, boolean revocationEnabled) throws Exception {
        PKIXBuilderParameters xparams = new PKIXBuilderParameters(trustStore, new X509CertSelector());
        if (crlf != null && crlf.length() > 0) {
            Collection<? extends CRL> crls = getCRLs(crlf);
            CertStoreParameters csp = new CollectionCertStoreParameters(crls);
            CertStore store = CertStore.getInstance("Collection", csp);
            xparams.addCertStore(store);
            xparams.setRevocationEnabled(true);
        } else {
            xparams.setRevocationEnabled(revocationEnabled);
        }
        xparams.setMaxPathLength(this.sslHostConfig.getCertificateVerificationDepth());
        return xparams;
    }

    protected Collection<? extends CRL> getCRLs(String crlf) throws IOException, CRLException, CertificateException {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream is = ConfigFileLoader.getSource().getResource(crlf).getInputStream();
            try {
                Collection<? extends CRL> crls = cf.generateCRLs(is);
                if (is != null) {
                    is.close();
                }
                return crls;
            } finally {
            }
        } catch (IOException | CRLException | CertificateException e) {
            throw e;
        }
    }
}
