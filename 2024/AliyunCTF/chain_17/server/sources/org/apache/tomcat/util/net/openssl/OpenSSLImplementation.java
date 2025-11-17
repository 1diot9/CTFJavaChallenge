package org.apache.tomcat.util.net.openssl;

import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLSession;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.apache.tomcat.util.net.SSLImplementation;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.SSLUtil;
import org.apache.tomcat.util.net.jsse.JSSESupport;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/openssl/OpenSSLImplementation.class */
public class OpenSSLImplementation extends SSLImplementation {
    @Override // org.apache.tomcat.util.net.SSLImplementation
    public SSLSupport getSSLSupport(SSLSession session, Map<String, List<String>> additionalAttributes) {
        return new JSSESupport(session, additionalAttributes);
    }

    @Override // org.apache.tomcat.util.net.SSLImplementation
    public SSLUtil getSSLUtil(SSLHostConfigCertificate certificate) {
        return new OpenSSLUtil(certificate);
    }
}
