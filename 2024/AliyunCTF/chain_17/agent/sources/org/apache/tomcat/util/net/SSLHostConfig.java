package org.apache.tomcat.util.net;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.security.KeyStore;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.management.ObjectName;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.apache.tomcat.util.net.openssl.OpenSSLConf;
import org.apache.tomcat.util.net.openssl.ciphers.Cipher;
import org.apache.tomcat.util.net.openssl.ciphers.OpenSSLCipherConfigurationParser;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.validation.DefaultBindingErrorProcessor;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/SSLHostConfig.class */
public class SSLHostConfig implements Serializable {
    private static final long serialVersionUID = 1;
    protected static final String DEFAULT_SSL_HOST_NAME = "_default_";
    public static final String DEFAULT_TLS_CIPHERS = "HIGH:!aNULL:!eNULL:!EXPORT:!DES:!RC4:!MD5:!kRSA";
    private String[] enabledCiphers;
    private String[] enabledProtocols;
    private ObjectName oname;
    private String certificateRevocationListFile;
    private String trustManagerClassName;
    private String certificateRevocationListPath;
    private String caCertificateFile;
    private String caCertificatePath;
    private static final Log log = LogFactory.getLog((Class<?>) SSLHostConfig.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) SSLHostConfig.class);
    protected static final Set<String> SSL_PROTO_ALL_SET = new HashSet();
    private Type configType = null;
    private String hostName = DEFAULT_SSL_HOST_NAME;
    private volatile transient Long openSslConfContext = 0L;
    private volatile transient Long openSslContext = 0L;
    private boolean tls13RenegotiationAvailable = false;
    private Set<String> explicitlyRequestedProtocols = new HashSet();
    private SSLHostConfigCertificate defaultCertificate = null;
    private Set<SSLHostConfigCertificate> certificates = new LinkedHashSet(4);
    private CertificateVerification certificateVerification = CertificateVerification.NONE;
    private int certificateVerificationDepth = 10;
    private boolean certificateVerificationDepthConfigured = false;
    private String ciphers = DEFAULT_TLS_CIPHERS;
    private LinkedHashSet<Cipher> cipherList = null;
    private List<String> jsseCipherNames = null;
    private boolean honorCipherOrder = false;
    private Set<String> protocols = new HashSet();
    private int sessionCacheSize = -1;
    private int sessionTimeout = 86400;
    private String keyManagerAlgorithm = KeyManagerFactory.getDefaultAlgorithm();
    private boolean revocationEnabled = false;
    private String sslProtocol = "TLS";
    private String truststoreAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
    private String truststoreFile = System.getProperty("javax.net.ssl.trustStore");
    private String truststorePassword = System.getProperty("javax.net.ssl.trustStorePassword");
    private String truststoreProvider = System.getProperty("javax.net.ssl.trustStoreProvider");
    private String truststoreType = System.getProperty("javax.net.ssl.trustStoreType");
    private transient KeyStore truststore = null;
    private boolean disableCompression = true;
    private boolean disableSessionTickets = false;
    private boolean insecureRenegotiation = false;
    private OpenSSLConf openSslConf = null;

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/SSLHostConfig$Type.class */
    public enum Type {
        JSSE,
        OPENSSL
    }

    static {
        SSL_PROTO_ALL_SET.add(Constants.SSL_PROTO_SSLv2Hello);
        SSL_PROTO_ALL_SET.add("TLSv1");
        SSL_PROTO_ALL_SET.add("TLSv1.1");
        SSL_PROTO_ALL_SET.add("TLSv1.2");
        SSL_PROTO_ALL_SET.add(Constants.SSL_PROTO_TLSv1_3);
    }

    public SSLHostConfig() {
        setProtocols(Constants.SSL_PROTO_ALL);
    }

    public boolean isTls13RenegotiationAvailable() {
        return this.tls13RenegotiationAvailable;
    }

    public void setTls13RenegotiationAvailable(boolean tls13RenegotiationAvailable) {
        this.tls13RenegotiationAvailable = tls13RenegotiationAvailable;
    }

    public Long getOpenSslConfContext() {
        return this.openSslConfContext;
    }

    public void setOpenSslConfContext(Long openSslConfContext) {
        this.openSslConfContext = openSslConfContext;
    }

    public Long getOpenSslContext() {
        return this.openSslContext;
    }

    public void setOpenSslContext(Long openSslContext) {
        this.openSslContext = openSslContext;
    }

    public String getConfigType() {
        return this.configType.name();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean setProperty(String name, Type configType) {
        if (this.configType == null) {
            this.configType = configType;
            return true;
        }
        if (configType != this.configType) {
            log.warn(sm.getString("sslHostConfig.mismatch", name, getHostName(), configType, this.configType));
            return false;
        }
        return true;
    }

    public String[] getEnabledProtocols() {
        return this.enabledProtocols;
    }

    public void setEnabledProtocols(String[] enabledProtocols) {
        this.enabledProtocols = enabledProtocols;
    }

    public String[] getEnabledCiphers() {
        return this.enabledCiphers;
    }

    public void setEnabledCiphers(String[] enabledCiphers) {
        this.enabledCiphers = enabledCiphers;
    }

    public ObjectName getObjectName() {
        return this.oname;
    }

    public void setObjectName(ObjectName oname) {
        this.oname = oname;
    }

    private void registerDefaultCertificate() {
        if (this.defaultCertificate == null) {
            SSLHostConfigCertificate defaultCertificate = new SSLHostConfigCertificate(this, SSLHostConfigCertificate.Type.UNDEFINED);
            addCertificate(defaultCertificate);
            this.defaultCertificate = defaultCertificate;
        }
    }

    public void addCertificate(SSLHostConfigCertificate certificate) {
        if (this.certificates.size() == 0) {
            this.certificates.add(certificate);
        } else {
            if ((this.certificates.size() == 1 && this.certificates.iterator().next().getType() == SSLHostConfigCertificate.Type.UNDEFINED) || certificate.getType() == SSLHostConfigCertificate.Type.UNDEFINED) {
                throw new IllegalArgumentException(sm.getString("sslHostConfig.certificate.notype"));
            }
            this.certificates.add(certificate);
        }
    }

    public OpenSSLConf getOpenSslConf() {
        return this.openSslConf;
    }

    public void setOpenSslConf(OpenSSLConf conf) {
        if (conf == null) {
            throw new IllegalArgumentException(sm.getString("sslHostConfig.opensslconf.null"));
        }
        if (this.openSslConf != null) {
            throw new IllegalArgumentException(sm.getString("sslHostConfig.opensslconf.alreadySet"));
        }
        setProperty("<OpenSSLConf>", Type.OPENSSL);
        this.openSslConf = conf;
    }

    public Set<SSLHostConfigCertificate> getCertificates() {
        return getCertificates(false);
    }

    public Set<SSLHostConfigCertificate> getCertificates(boolean createDefaultIfEmpty) {
        if (this.certificates.size() == 0 && createDefaultIfEmpty) {
            registerDefaultCertificate();
        }
        return this.certificates;
    }

    public void setCertificateRevocationListFile(String certificateRevocationListFile) {
        this.certificateRevocationListFile = certificateRevocationListFile;
    }

    public String getCertificateRevocationListFile() {
        return this.certificateRevocationListFile;
    }

    public void setCertificateVerification(String certificateVerification) {
        try {
            this.certificateVerification = CertificateVerification.fromString(certificateVerification);
        } catch (IllegalArgumentException iae) {
            this.certificateVerification = CertificateVerification.REQUIRED;
            throw iae;
        }
    }

    public CertificateVerification getCertificateVerification() {
        return this.certificateVerification;
    }

    public void setCertificateVerificationAsString(String certificateVerification) {
        setCertificateVerification(certificateVerification);
    }

    public String getCertificateVerificationAsString() {
        return this.certificateVerification.toString();
    }

    public void setCertificateVerificationDepth(int certificateVerificationDepth) {
        this.certificateVerificationDepth = certificateVerificationDepth;
        this.certificateVerificationDepthConfigured = true;
    }

    public int getCertificateVerificationDepth() {
        return this.certificateVerificationDepth;
    }

    public boolean isCertificateVerificationDepthConfigured() {
        return this.certificateVerificationDepthConfigured;
    }

    public void setCiphers(String ciphersList) {
        if (ciphersList != null && !ciphersList.contains(":")) {
            StringBuilder sb = new StringBuilder();
            String[] ciphers = ciphersList.split(",");
            for (String cipher : ciphers) {
                String trimmed = cipher.trim();
                if (trimmed.length() > 0) {
                    String openSSLName = OpenSSLCipherConfigurationParser.jsseToOpenSSL(trimmed);
                    if (openSSLName == null) {
                        openSSLName = trimmed;
                    }
                    if (sb.length() > 0) {
                        sb.append(':');
                    }
                    sb.append(openSSLName);
                }
            }
            this.ciphers = sb.toString();
        } else {
            this.ciphers = ciphersList;
        }
        this.cipherList = null;
        this.jsseCipherNames = null;
    }

    public String getCiphers() {
        return this.ciphers;
    }

    public LinkedHashSet<Cipher> getCipherList() {
        if (this.cipherList == null) {
            this.cipherList = OpenSSLCipherConfigurationParser.parse(getCiphers());
        }
        return this.cipherList;
    }

    public List<String> getJsseCipherNames() {
        if (this.jsseCipherNames == null) {
            this.jsseCipherNames = OpenSSLCipherConfigurationParser.convertForJSSE(getCipherList());
        }
        return this.jsseCipherNames;
    }

    public void setHonorCipherOrder(boolean honorCipherOrder) {
        this.honorCipherOrder = honorCipherOrder;
    }

    public boolean getHonorCipherOrder() {
        return this.honorCipherOrder;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName.toLowerCase(Locale.ENGLISH);
    }

    public String getHostName() {
        return this.hostName;
    }

    public void setProtocols(String input) {
        this.protocols.clear();
        this.explicitlyRequestedProtocols.clear();
        for (String value : input.split("(?=[-+,])")) {
            String trimmed = value.trim();
            if (trimmed.length() > 1) {
                if (trimmed.charAt(0) == '+') {
                    String trimmed2 = trimmed.substring(1).trim();
                    if (trimmed2.equalsIgnoreCase(Constants.SSL_PROTO_ALL)) {
                        this.protocols.addAll(SSL_PROTO_ALL_SET);
                    } else {
                        this.protocols.add(trimmed2);
                        this.explicitlyRequestedProtocols.add(trimmed2);
                    }
                } else if (trimmed.charAt(0) == '-') {
                    String trimmed3 = trimmed.substring(1).trim();
                    if (trimmed3.equalsIgnoreCase(Constants.SSL_PROTO_ALL)) {
                        this.protocols.removeAll(SSL_PROTO_ALL_SET);
                    } else {
                        this.protocols.remove(trimmed3);
                        this.explicitlyRequestedProtocols.remove(trimmed3);
                    }
                } else {
                    if (trimmed.charAt(0) == ',') {
                        trimmed = trimmed.substring(1).trim();
                    }
                    if (!this.protocols.isEmpty()) {
                        log.warn(sm.getString("sslHostConfig.prefix_missing", trimmed, getHostName()));
                    }
                    if (trimmed.equalsIgnoreCase(Constants.SSL_PROTO_ALL)) {
                        this.protocols.addAll(SSL_PROTO_ALL_SET);
                    } else {
                        this.protocols.add(trimmed);
                        this.explicitlyRequestedProtocols.add(trimmed);
                    }
                }
            }
        }
    }

    public Set<String> getProtocols() {
        return this.protocols;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isExplicitlyRequestedProtocol(String protocol) {
        return this.explicitlyRequestedProtocols.contains(protocol);
    }

    public void setSessionCacheSize(int sessionCacheSize) {
        this.sessionCacheSize = sessionCacheSize;
    }

    public int getSessionCacheSize() {
        return this.sessionCacheSize;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public int getSessionTimeout() {
        return this.sessionTimeout;
    }

    public void setKeyManagerAlgorithm(String keyManagerAlgorithm) {
        setProperty("keyManagerAlgorithm", Type.JSSE);
        this.keyManagerAlgorithm = keyManagerAlgorithm;
    }

    public String getKeyManagerAlgorithm() {
        return this.keyManagerAlgorithm;
    }

    public void setRevocationEnabled(boolean revocationEnabled) {
        setProperty("revocationEnabled", Type.JSSE);
        this.revocationEnabled = revocationEnabled;
    }

    public boolean getRevocationEnabled() {
        return this.revocationEnabled;
    }

    public void setSslProtocol(String sslProtocol) {
        setProperty("sslProtocol", Type.JSSE);
        this.sslProtocol = sslProtocol;
    }

    public String getSslProtocol() {
        return this.sslProtocol;
    }

    public void setTrustManagerClassName(String trustManagerClassName) {
        setProperty("trustManagerClassName", Type.JSSE);
        this.trustManagerClassName = trustManagerClassName;
    }

    public String getTrustManagerClassName() {
        return this.trustManagerClassName;
    }

    public void setTruststoreAlgorithm(String truststoreAlgorithm) {
        setProperty("truststoreAlgorithm", Type.JSSE);
        this.truststoreAlgorithm = truststoreAlgorithm;
    }

    public String getTruststoreAlgorithm() {
        return this.truststoreAlgorithm;
    }

    public void setTruststoreFile(String truststoreFile) {
        setProperty("truststoreFile", Type.JSSE);
        this.truststoreFile = truststoreFile;
    }

    public String getTruststoreFile() {
        return this.truststoreFile;
    }

    public void setTruststorePassword(String truststorePassword) {
        setProperty("truststorePassword", Type.JSSE);
        this.truststorePassword = truststorePassword;
    }

    public String getTruststorePassword() {
        return this.truststorePassword;
    }

    public void setTruststoreProvider(String truststoreProvider) {
        setProperty("truststoreProvider", Type.JSSE);
        this.truststoreProvider = truststoreProvider;
    }

    public String getTruststoreProvider() {
        if (this.truststoreProvider == null) {
            Set<SSLHostConfigCertificate> certificates = getCertificates();
            if (certificates.size() == 1) {
                return certificates.iterator().next().getCertificateKeystoreProvider();
            }
            return SSLHostConfigCertificate.DEFAULT_KEYSTORE_PROVIDER;
        }
        return this.truststoreProvider;
    }

    public void setTruststoreType(String truststoreType) {
        setProperty("truststoreType", Type.JSSE);
        this.truststoreType = truststoreType;
    }

    public String getTruststoreType() {
        if (this.truststoreType == null) {
            Set<SSLHostConfigCertificate> certificates = getCertificates();
            if (certificates.size() == 1) {
                String keystoreType = certificates.iterator().next().getCertificateKeystoreType();
                if (!"PKCS12".equalsIgnoreCase(keystoreType)) {
                    return keystoreType;
                }
            }
            return SSLHostConfigCertificate.DEFAULT_KEYSTORE_TYPE;
        }
        return this.truststoreType;
    }

    public void setTrustStore(KeyStore truststore) {
        this.truststore = truststore;
    }

    public KeyStore getTruststore() throws IOException {
        KeyStore result = this.truststore;
        if (result == null && this.truststoreFile != null) {
            try {
                result = SSLUtilBase.getStore(getTruststoreType(), getTruststoreProvider(), getTruststoreFile(), getTruststorePassword(), null);
            } catch (IOException ioe) {
                Throwable cause = ioe.getCause();
                if (cause instanceof UnrecoverableKeyException) {
                    log.warn(sm.getString("sslHostConfig.invalid_truststore_password"), cause);
                    result = SSLUtilBase.getStore(getTruststoreType(), getTruststoreProvider(), getTruststoreFile(), null, null);
                } else {
                    throw ioe;
                }
            }
        }
        return result;
    }

    public void setCertificateRevocationListPath(String certificateRevocationListPath) {
        setProperty("certificateRevocationListPath", Type.OPENSSL);
        this.certificateRevocationListPath = certificateRevocationListPath;
    }

    public String getCertificateRevocationListPath() {
        return this.certificateRevocationListPath;
    }

    public void setCaCertificateFile(String caCertificateFile) {
        if (setProperty("caCertificateFile", Type.OPENSSL) && this.truststoreFile != null) {
            this.truststoreFile = null;
        }
        this.caCertificateFile = caCertificateFile;
    }

    public String getCaCertificateFile() {
        return this.caCertificateFile;
    }

    public void setCaCertificatePath(String caCertificatePath) {
        if (setProperty("caCertificatePath", Type.OPENSSL) && this.truststoreFile != null) {
            this.truststoreFile = null;
        }
        this.caCertificatePath = caCertificatePath;
    }

    public String getCaCertificatePath() {
        return this.caCertificatePath;
    }

    public void setDisableCompression(boolean disableCompression) {
        setProperty("disableCompression", Type.OPENSSL);
        this.disableCompression = disableCompression;
    }

    public boolean getDisableCompression() {
        return this.disableCompression;
    }

    public void setDisableSessionTickets(boolean disableSessionTickets) {
        setProperty("disableSessionTickets", Type.OPENSSL);
        this.disableSessionTickets = disableSessionTickets;
    }

    public boolean getDisableSessionTickets() {
        return this.disableSessionTickets;
    }

    public void setInsecureRenegotiation(boolean insecureRenegotiation) {
        setProperty("insecureRenegotiation", Type.OPENSSL);
        this.insecureRenegotiation = insecureRenegotiation;
    }

    public boolean getInsecureRenegotiation() {
        return this.insecureRenegotiation;
    }

    public Set<X509Certificate> certificatesExpiringBefore(Date date) {
        Set<X509Certificate> result = new HashSet<>();
        Set<SSLHostConfigCertificate> sslHostConfigCertificates = getCertificates();
        for (SSLHostConfigCertificate sslHostConfigCertificate : sslHostConfigCertificates) {
            SSLContext sslContext = sslHostConfigCertificate.getSslContext();
            if (sslContext != null) {
                String alias = sslHostConfigCertificate.getCertificateKeyAlias();
                if (alias == null) {
                    alias = SSLUtilBase.DEFAULT_KEY_ALIAS;
                }
                X509Certificate[] certificates = sslContext.getCertificateChain(alias);
                if (certificates != null && certificates.length > 0) {
                    X509Certificate certificate = certificates[0];
                    Date expirationDate = certificate.getNotAfter();
                    if (date.after(expirationDate)) {
                        result.add(certificate);
                    }
                }
            }
        }
        return result;
    }

    public static String adjustRelativePath(String path) throws FileNotFoundException {
        if (path == null || path.length() == 0) {
            return path;
        }
        String newPath = path;
        File f = new File(newPath);
        if (!f.isAbsolute()) {
            newPath = System.getProperty("catalina.base") + File.separator + newPath;
            f = new File(newPath);
        }
        if (!f.exists()) {
            throw new FileNotFoundException(sm.getString("sslHostConfig.fileNotFound", newPath));
        }
        return newPath;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/SSLHostConfig$CertificateVerification.class */
    public enum CertificateVerification {
        NONE(false),
        OPTIONAL_NO_CA(true),
        OPTIONAL(true),
        REQUIRED(false);

        private final boolean optional;

        CertificateVerification(boolean optional) {
            this.optional = optional;
        }

        public boolean isOptional() {
            return this.optional;
        }

        public static CertificateVerification fromString(String value) {
            if ("true".equalsIgnoreCase(value) || CustomBooleanEditor.VALUE_YES.equalsIgnoreCase(value) || "require".equalsIgnoreCase(value) || DefaultBindingErrorProcessor.MISSING_FIELD_ERROR_CODE.equalsIgnoreCase(value)) {
                return REQUIRED;
            }
            if ("optional".equalsIgnoreCase(value) || "want".equalsIgnoreCase(value)) {
                return OPTIONAL;
            }
            if ("optionalNoCA".equalsIgnoreCase(value) || "optional_no_ca".equalsIgnoreCase(value)) {
                return OPTIONAL_NO_CA;
            }
            if ("false".equalsIgnoreCase(value) || "no".equalsIgnoreCase(value) || "none".equalsIgnoreCase(value)) {
                return NONE;
            }
            throw new IllegalArgumentException(SSLHostConfig.sm.getString("sslHostConfig.certificateVerificationInvalid", value));
        }
    }
}
