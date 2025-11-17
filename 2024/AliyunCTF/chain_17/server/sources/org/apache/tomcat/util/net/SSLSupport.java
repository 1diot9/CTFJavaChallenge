package org.apache.tomcat.util.net;

import java.io.IOException;
import java.security.cert.X509Certificate;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/SSLSupport.class */
public interface SSLSupport {
    public static final String CIPHER_SUITE_KEY = "jakarta.servlet.request.cipher_suite";
    public static final String KEY_SIZE_KEY = "jakarta.servlet.request.key_size";
    public static final String CERTIFICATE_KEY = "jakarta.servlet.request.X509Certificate";
    public static final String SESSION_ID_KEY = "jakarta.servlet.request.ssl_session_id";
    public static final String SESSION_MGR = "jakarta.servlet.request.ssl_session_mgr";
    public static final String PROTOCOL_VERSION_KEY = "org.apache.tomcat.util.net.secure_protocol_version";
    public static final String REQUESTED_CIPHERS_KEY = "org.apache.tomcat.util.net.secure_requested_ciphers";
    public static final String REQUESTED_PROTOCOL_VERSIONS_KEY = "org.apache.tomcat.util.net.secure_requested_protocol_versions";

    String getCipherSuite() throws IOException;

    X509Certificate[] getPeerCertificateChain() throws IOException;

    Integer getKeySize() throws IOException;

    String getSessionId() throws IOException;

    String getProtocol() throws IOException;

    String getRequestedProtocols() throws IOException;

    String getRequestedCiphers() throws IOException;

    default X509Certificate[] getLocalCertificateChain() {
        return null;
    }
}
