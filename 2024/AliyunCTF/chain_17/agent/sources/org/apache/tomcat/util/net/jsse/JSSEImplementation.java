package org.apache.tomcat.util.net.jsse;

import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLSession;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.apache.tomcat.util.net.SSLImplementation;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.SSLUtil;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/jsse/JSSEImplementation.class */
public class JSSEImplementation extends SSLImplementation {
    public JSSEImplementation() {
        JSSESupport.init();
    }

    @Override // org.apache.tomcat.util.net.SSLImplementation
    public SSLSupport getSSLSupport(SSLSession session, Map<String, List<String>> additionalAttributes) {
        return new JSSESupport(session, additionalAttributes);
    }

    @Override // org.apache.tomcat.util.net.SSLImplementation
    public SSLUtil getSSLUtil(SSLHostConfigCertificate certificate) {
        return new JSSEUtil(certificate);
    }
}
