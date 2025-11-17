package org.apache.tomcat.util.net;

import java.util.List;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/SSLUtil.class */
public interface SSLUtil {

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/SSLUtil$ProtocolInfo.class */
    public interface ProtocolInfo {
        String getNegotiatedProtocol();
    }

    SSLContext createSSLContext(List<String> list) throws Exception;

    KeyManager[] getKeyManagers() throws Exception;

    TrustManager[] getTrustManagers() throws Exception;

    void configureSessionContext(SSLSessionContext sSLSessionContext);

    String[] getEnabledProtocols() throws IllegalArgumentException;

    String[] getEnabledCiphers() throws IllegalArgumentException;
}
